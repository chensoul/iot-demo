package com.iot.demo.access.controller;

import com.iot.demo.backend.domain.device.DeviceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/openapi/devices")
@RequiredArgsConstructor
public class DeviceOpenApiController {
    private final DeviceService deviceService;

    @PostMapping("/register")
    public Map<String, Object> register(HttpServletRequest request) {
        return deviceService.register(request);
    }

    @PostMapping("/online")
    public Map<String, Object> online(HttpServletRequest request) {
        return deviceService.online(request);
    }

    @PostMapping("/offline")
    public Map<String, Object> offline(HttpServletRequest request) {
        return deviceService.offline(request);
    }

    @PostMapping("/heartbeat")
    public Map<String, Object> heartbeat(HttpServletRequest request) {
        return deviceService.heartbeat(request);
    }
}
