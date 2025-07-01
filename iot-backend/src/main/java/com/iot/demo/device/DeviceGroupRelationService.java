package com.iot.demo.device;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceGroupRelationService {
    private final DeviceGroupRelationRepository relationRepository;

    public List<DeviceGroupRelation> findByDeviceId(String deviceId) {
        return relationRepository.findByDeviceId(deviceId);
    }

    public List<DeviceGroupRelation> findByGroupId(Long groupId) {
        return relationRepository.findByGroupId(groupId);
    }

    @Transactional(rollbackFor = Exception.class)
    public DeviceGroupRelation assign(String deviceId, Long groupId) {
        DeviceGroupRelation relation = new DeviceGroupRelation();
        relation.setDeviceId(deviceId);
        relation.setGroupId(groupId);
        return relationRepository.save(relation);
    }

    @Transactional(rollbackFor = Exception.class)
    public void remove(String deviceId, Long groupId) {
        relationRepository.deleteByDeviceIdAndGroupId(deviceId, groupId);
    }
} 