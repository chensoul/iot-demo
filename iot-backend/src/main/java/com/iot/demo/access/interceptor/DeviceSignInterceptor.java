package com.iot.demo.access.interceptor;

import com.iot.demo.backend.domain.device.Device;
import com.iot.demo.backend.domain.device.DeviceRepository;
import com.iot.demo.backend.domain.product.Product;
import com.iot.demo.backend.domain.product.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.security.sasl.AuthenticationException;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceSignInterceptor implements HandlerInterceptor {
    private final ProductRepository productRepository;
    private final DeviceRepository deviceRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        try {
            doHandler(request);
        } catch (AuthenticationException e) {
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":401,\"msg\":\"签名校验失败\"}");
            return false;
        } catch (Exception e) {
            response.setStatus(400);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":400,\"msg\":\"" + e.getMessage() + "\"}");
            return false;
        }
        // 校验通过
        return true;
    }

    public void doHandler(HttpServletRequest request) throws Exception {
        String productKey = request.getHeader("X-Product-Key");
        String deviceName = request.getHeader("X-Device-Name");
        String deviceSecret = request.getHeader("X-Device-Secret");
        String sign = request.getHeader("X-Sign");
        String nonce = request.getHeader("X-Nonce");
        Object tsObj = request.getHeader("X-Timestamp");
        long timestamp = tsObj instanceof Number ? ((Number) tsObj).longValue() : Long.parseLong(tsObj.toString());
        if (productKey == null || deviceName == null || sign == null || nonce == null || timestamp == 0) {
            throw new IllegalArgumentException("参数不完整");
        }

        Product product = productRepository.findByProductKey(productKey);
        if (product == null) {
            throw new IllegalArgumentException("产品不存在");
        }

        String secret = Objects.toString(deviceSecret, product.getProductSecret());
        String raw = productKey + deviceName + timestamp + nonce + secret;
        String expectSign = DigestUtils.md5DigestAsHex(raw.getBytes());
        if (!expectSign.equalsIgnoreCase(sign)) {
            throw new AuthenticationException("签名校验失败");
        }

        // 幂等查找
        Device device = deviceRepository.findByProductIdAndName(product.getId(), deviceName);
        if (deviceSecret != null) { // 预注册校验
            log.info("对设备 {} 预注册校验", deviceName);

            if (device == null) {
                throw new IllegalArgumentException("设备未预注册");
            }
            if (!deviceSecret.equals(device.getDeviceSecret())) {
                throw new IllegalArgumentException("设备密钥错误");
            }
        } else {
            log.info("对设备 {} 动态注册校验", deviceName);
        }

        //上线、下线、心跳时，检查设备是否禁用
        if (device != null) {
            log.info("设备 {} 已注册", deviceName);

            if (!device.isEnabled()) {
                throw new IllegalArgumentException("设备已被禁用");
            }
        }

        request.getSession().setAttribute("productId", product.getId());
        request.getSession().setAttribute("device", device);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        request.getSession().removeAttribute("productId");
        request.getSession().removeAttribute("device");
    }
}
