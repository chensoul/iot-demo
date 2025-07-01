package com.iot.demo.backend.domain.device;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "device")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 64)
    private String deviceSecret;

    @Column(nullable = false, length = 40)
    private String name;

    @Column(nullable = false)
    private String productId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private DeviceStatusEnum status;

    private boolean enabled; // 启用/禁用

    private String ip;

    @Column(length = 32)
    private String firmwareVersion;

    @CreationTimestamp
    private LocalDateTime createTime;

    @UpdateTimestamp
    private LocalDateTime updateTime;

    private LocalDateTime activeTime;

    private LocalDateTime lastOnlineTime;

    private LocalDateTime lastOfflineTime;


    @Column(columnDefinition = "json")
    private String tags; // JSON 格式存储标签，如 {"a":"1","b":"2"}
} 