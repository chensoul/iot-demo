package com.iot.demo.device;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "device_group_relation")
public class DeviceGroupRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 36)
    private String deviceId;

    @Column(nullable = false)
    private Long groupId;
} 