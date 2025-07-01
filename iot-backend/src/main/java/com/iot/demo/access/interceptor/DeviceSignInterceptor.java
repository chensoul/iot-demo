package com.iot.demo.access.interceptor;

import com.iot.demo.backend.domain.device.Device;
import com.iot.demo.backend.domain.device.DeviceRepository;
import com.iot.demo.backend.domain.product.Product;
import com.iot.demo.backend.domain.product.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.security.sasl.AuthenticationException;
import java.util.Objects;

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
        String productKey = request.getHeader("productKey");
        String productSecret = request.getHeader("productSecret");
        String deviceName = request.getHeader("deviceName");
        String deviceSecret = request.getHeader("deviceSecret");
        String sign = request.getHeader("sign");
        String nonce = request.getHeader("nonce");
        Object tsObj = request.getHeader("timestamp");
        long timestamp = tsObj instanceof Number ? ((Number) tsObj).longValue() : Long.parseLong(tsObj.toString());
        if (productKey == null || deviceName == null || sign == null || nonce == null || timestamp == 0) {
            throw new IllegalArgumentException("参数不完整");
        }

        String secret = Objects.toString(deviceSecret, productSecret);
        if (!StringUtils.hasText(secret)) {
            throw new IllegalArgumentException("缺少密钥参数");
        }

        String raw = productKey + deviceName + timestamp + nonce + secret;
        String expectSign = DigestUtils.md5DigestAsHex(raw.getBytes());
        if (!expectSign.equalsIgnoreCase(sign)) {
            throw new AuthenticationException("签名校验失败");
        }

        Product product = productRepository.findByProductKey(productKey);
        if (product == null) {
            throw new IllegalArgumentException("产品不存在");
        }

        // 幂等查找
        Device device = deviceRepository.findByProductIdAndName(product.getId(), deviceName);
        if (deviceSecret != null) { // 预注册校验
            if (device == null) {
                throw new IllegalArgumentException("设备未预注册");
            }
            if (!deviceSecret.equals(device.getDeviceSecret())) {
                throw new IllegalArgumentException("设备密钥错误");
            }
        }

        //上线、下线、心跳时，检查设备是否禁用
        if (device != null) {
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
