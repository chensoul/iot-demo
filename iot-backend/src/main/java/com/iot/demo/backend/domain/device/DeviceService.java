package com.iot.demo.backend.domain.device;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.demo.backend.domain.product.Product;
import com.iot.demo.backend.domain.product.ProductService;
import com.iot.demo.backend.util.NanoIdUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final ProductService productService;
    private final ObjectMapper objectMapper;
    private final EntityManager entityManager;
    private final DeviceRepository deviceRepository;

    public List<Device> list() {
        return deviceRepository.findAll();
    }

    public Optional<Device> getById(String id) {
        return deviceRepository.findById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public Device create(Device device) {
        Product product = productService.getById(device.getProductId()).orElseThrow(() -> new IllegalArgumentException("产品不存在"));
        device.setStatus(DeviceStatusEnum.UNACTIVE);
        device.setDeviceSecret(NanoIdUtils.randomUUID());
        return deviceRepository.save(device);
    }

    @Transactional(rollbackFor = Exception.class)
    public Device update(String id, Device update) {
        Device device = deviceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("设备不存在"));
        device.setName(update.getName());
        device.setStatus(update.getStatus());
        device.setIp(update.getIp());
        device.setFirmwareVersion(update.getFirmwareVersion());
        // 可补充其他字段
        return deviceRepository.save(device);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        deviceRepository.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public Device enable(String id) {
        Device device = deviceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("设备不存在"));
        device.setEnabled(true);
        return deviceRepository.save(device);
    }

    @Transactional(rollbackFor = Exception.class)
    public Device disable(String id) {
        Device device = deviceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("设备不存在"));
        device.setEnabled(false);
        return deviceRepository.save(device);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateTags(String id, Map<String, String> tags) {
        Device device = deviceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("设备不存在"));
        try {
            device.setTags(objectMapper.writeValueAsString(tags));
        } catch (Exception e) {
            throw new RuntimeException("标签序列化失败", e);
        }
        deviceRepository.save(device);
    }

    public Map<String, String> getTags(String id) {
        Device device = deviceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("设备不存在"));
        try {
            if (device.getTags() == null) {
                return java.util.Collections.emptyMap();
            }
            return objectMapper.readValue(device.getTags(), new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("标签反序列化失败", e);
        }
    }

    public List<Device> findByTags(Map<String, String> tags) {
        if (tags == null || tags.isEmpty()) {
            return deviceRepository.findAll();
        }
        StringBuilder sql = new StringBuilder("SELECT * FROM device WHERE 1=1 ");
        for (String key : tags.keySet()) {
            sql.append(" AND JSON_EXTRACT(tags, '$.").append(key).append("') = :").append(key);
        }
        Query query = entityManager.createNativeQuery(sql.toString(), Device.class);
        for (Map.Entry<String, String> entry : tags.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> register(HttpServletRequest request) {
        String productId = request.getSession().getAttribute("productId").toString();
        String deviceName = request.getHeader("deviceName");
        Device device = (Device) request.getSession().getAttribute("device");

        if (device == null) {
            device = new Device();
            device.setName(deviceName);
            device.setProductId(productId);
            device.setDeviceSecret(NanoIdUtils.randomUUID());
            device.setStatus(DeviceStatusEnum.ACTIVE);
            device = deviceRepository.save(device);
        }
        HashMap<String, Object> result = new HashMap<>();
        result.put("deviceId", device.getId());
        result.put("deviceSecret", device.getDeviceSecret());
        result.put("status", device.getStatus());
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> online(HttpServletRequest request) {
        Device device = (Device) request.getSession().getAttribute("device");
        if (device == null) {
            throw new IllegalArgumentException("设备不存在");
        }

        // 激活与上线状态流转
        boolean changed = false;
        if (device.getStatus() == DeviceStatusEnum.UNACTIVE) {
            device.setStatus(DeviceStatusEnum.ACTIVE);
            device.setActiveTime(LocalDateTime.now());
            changed = true;
        }
        if (device.getStatus() != DeviceStatusEnum.ONLINE) {
            device.setStatus(DeviceStatusEnum.ONLINE);
            device.setLastOnlineTime(LocalDateTime.now());
            changed = true;
        }
        if (changed) {
            deviceRepository.save(device);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", device.getId());
        data.put("status", device.getStatus().name());
        return data;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> offline(HttpServletRequest request) {
        Device device = (Device) request.getSession().getAttribute("device");
        if (device == null) {
            throw new IllegalArgumentException("设备不存在");
        }

        // 激活与上线状态流转
        boolean changed = false;
        if (device.getStatus() == DeviceStatusEnum.UNACTIVE) {
            device.setStatus(DeviceStatusEnum.ACTIVE);
            device.setActiveTime(LocalDateTime.now());
            changed = true;
        }
        if (device.getStatus() != DeviceStatusEnum.OFFLINE) {
            device.setStatus(DeviceStatusEnum.OFFLINE);
            device.setLastOfflineTime(LocalDateTime.now());
            changed = true;
        }
        if (changed) {
            deviceRepository.save(device);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", device.getId());
        data.put("status", device.getStatus().name());
        return data;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> heartbeat(HttpServletRequest request) {
        Device device = (Device) request.getSession().getAttribute("device");
        if (device == null) {
            throw new IllegalArgumentException("设备不存在");
        }
        if (device.getStatus() == DeviceStatusEnum.UNACTIVE) {
            throw new IllegalArgumentException("设备未激活，不能心跳");
        }
        device.setLastOnlineTime(LocalDateTime.now());
        device.setStatus(DeviceStatusEnum.ONLINE);
        deviceRepository.save(device);

        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", device.getId());
        data.put("status", device.getStatus().name());
        return data;
    }
} 