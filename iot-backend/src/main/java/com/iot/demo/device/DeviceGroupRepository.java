package com.iot.demo.device;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeviceGroupRepository extends JpaRepository<DeviceGroup, Long> {
    List<DeviceGroup> findByParentId(Long parentId);
} 