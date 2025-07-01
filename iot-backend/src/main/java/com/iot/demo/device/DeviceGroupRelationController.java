package com.iot.demo.device;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/device-group-relations")
@RequiredArgsConstructor
public class DeviceGroupRelationController {
    private final DeviceGroupRelationService relationService;

    @GetMapping("/device/{deviceId}")
    public List<DeviceGroupRelation> findByDeviceId(@PathVariable String deviceId) {
        return relationService.findByDeviceId(deviceId);
    }

    @GetMapping("/group/{groupId}")
    public List<DeviceGroupRelation> findByGroupId(@PathVariable Long groupId) {
        return relationService.findByGroupId(groupId);
    }

    @PostMapping
    public DeviceGroupRelation assign(@RequestParam String deviceId, @RequestParam Long groupId) {
        return relationService.assign(deviceId, groupId);
    }

    @DeleteMapping
    public void remove(@RequestParam String deviceId, @RequestParam Long groupId) {
        relationService.remove(deviceId, groupId);
    }
} 