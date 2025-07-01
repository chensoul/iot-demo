package com.iot.demo.device;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
} 