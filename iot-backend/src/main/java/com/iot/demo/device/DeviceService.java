package com.iot.demo.device;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.demo.product.Product;
import com.iot.demo.product.ProductService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final ProductService productService;
    private final ObjectMapper objectMapper;
    private final EntityManager entityManager;

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
        device.setDeviceSecret(UUID.randomUUID().toString().replaceAll("-", ""));
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
        device.setStatus(DeviceStatusEnum.ENABLED);
        return deviceRepository.save(device);
    }

    @Transactional(rollbackFor = Exception.class)
    public Device disable(String id) {
        Device device = deviceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("设备不存在"));
        device.setStatus(DeviceStatusEnum.DISABLED);
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
} 