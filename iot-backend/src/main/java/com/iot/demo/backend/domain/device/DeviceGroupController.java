package com.iot.demo.backend.domain.device;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/device-groups")
@RequiredArgsConstructor
public class DeviceGroupController {
    private final DeviceGroupService groupService;
    private final DeviceGroupRelationService relationService;

    @GetMapping
    public List<DeviceGroup> list() {
        return groupService.list();
    }

    @GetMapping("/tree")
    public List<DeviceGroupTreeDTO> getGroupTree() {
        return groupService.getGroupTree();
    }

    @GetMapping("/tree-with-devices")
    public List<DeviceGroupTreeWithDevicesDTO> getGroupTreeWithDevices() {
        return groupService.getGroupTreeWithDevices();
    }

    @GetMapping("/{groupId}")
    public DeviceGroup getById(@PathVariable Long groupId) {
        return groupService.getById(groupId).orElse(null);
    }

    @GetMapping("/parent/{parentId}")
    public List<DeviceGroup> getChildren(@PathVariable Long parentId) {
        return groupService.getChildren(parentId);
    }

    @PostMapping
    public DeviceGroup create(@RequestBody DeviceGroup group) {
        return groupService.create(group);
    }

    @PutMapping("/{groupId}")
    public DeviceGroup update(@PathVariable Long groupId, @RequestBody DeviceGroup group) {
        return groupService.update(groupId, group);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        groupService.delete(id);
    }

    @PostMapping("/{groupId}/bind")
    public DeviceGroupRelation bind(@PathVariable Long groupId, @RequestParam String deviceId) {
        return relationService.bind(deviceId, groupId);
    }

    @PostMapping("/{groupId}/unbind")
    public void unbind(@PathVariable Long groupId, @RequestParam String deviceId) {
        relationService.unbind(deviceId, groupId);
    }
}