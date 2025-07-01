package com.iot.demo.device;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeviceGroupRelationRepository extends JpaRepository<DeviceGroupRelation, Long> {
    List<DeviceGroupRelation> findByDeviceId(String deviceId);
    List<DeviceGroupRelation> findByGroupId(Long groupId);
    void deleteByDeviceIdAndGroupId(String deviceId, Long groupId);
} 