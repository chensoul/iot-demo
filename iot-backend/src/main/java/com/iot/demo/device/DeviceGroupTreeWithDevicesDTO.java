package com.iot.demo.device;

import lombok.Data;

import java.util.List;

@Data
public class DeviceGroupTreeWithDevicesDTO {
    private Long id;
    private String name;
    private Long parentId;
    private String description;
    private List<DeviceGroupTreeWithDevicesDTO> children;
    private List<Device> devices;
} 