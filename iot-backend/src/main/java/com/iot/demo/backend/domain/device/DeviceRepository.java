package com.iot.demo.backend.domain.device;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, String> {
    Device findByProductIdAndName(String productId, String name);
}
