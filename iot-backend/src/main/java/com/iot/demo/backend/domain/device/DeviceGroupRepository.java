package com.iot.demo.backend.domain.device;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeviceGroupRepository extends JpaRepository<DeviceGroup, Long> {
    List<DeviceGroup> findByParentId(Long parentId);
} 