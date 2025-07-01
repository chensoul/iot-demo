package com.iot.demo.device;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;

    @GetMapping
    public List<Device> list() {
        return deviceService.list();
    }

    @GetMapping("/{id}")
    public Device getById(@PathVariable String id) {
        return deviceService.getById(id).orElse(null);
    }

    @PostMapping
    public Device create(@RequestBody Device device) {
        return deviceService.create(device);
    }

    @PutMapping("/{id}")
    public Device update(@PathVariable String id, @RequestBody Device device) {
        return deviceService.update(id, device);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        deviceService.delete(id);
    }

    @PutMapping("/{id}/enable")
    public Device enable(@PathVariable String id) {
        return deviceService.enable(id);
    }

    @PutMapping("/{id}/disable")
    public Device disable(@PathVariable String id) {
        return deviceService.disable(id);
    }

    @PutMapping("/{id}/tags")
    public void updateTags(@PathVariable String id, @RequestBody Map<String, String> tags) {
        deviceService.updateTags(id, tags);
    }

    @GetMapping("/{id}/tags")
    public Map<String, String> getTags(@PathVariable String id) {
        return deviceService.getTags(id);
    }

    @GetMapping("/tags")
    public List<Device> queryByTags(@RequestBody Map<String, String> tags) {
        return deviceService.findByTags(tags);
    }
} 