package com.iot.demo.device;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/device-groups")
@RequiredArgsConstructor
public class DeviceGroupController {
    private final DeviceGroupService groupService;

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

    @GetMapping("/{id}")
    public DeviceGroup getById(@PathVariable Long id) {
        return groupService.getById(id).orElse(null);
    }

    @GetMapping("/parent/{parentId}")
    public List<DeviceGroup> getChildren(@PathVariable Long parentId) {
        return groupService.getChildren(parentId);
    }

    @PostMapping
    public DeviceGroup create(@RequestBody DeviceGroup group) {
        return groupService.create(group);
    }

    @PutMapping("/{id}")
    public DeviceGroup update(@PathVariable Long id, @RequestBody DeviceGroup group) {
        return groupService.update(id, group);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        groupService.delete(id);
    }
}