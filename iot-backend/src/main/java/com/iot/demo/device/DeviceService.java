package com.iot.demo.device;

import com.iot.demo.product.Product;
import com.iot.demo.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final ProductService productService;

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
} 