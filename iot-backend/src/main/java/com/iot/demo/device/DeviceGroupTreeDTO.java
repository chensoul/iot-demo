package com.iot.demo.device;

import lombok.Data;

import java.util.List;

@Data
public class DeviceGroupTreeDTO {
    private Long id;
    private String name;
    private Long parentId;
    private String description;
    private List<DeviceGroupTreeDTO> children;
}