package com.iot.demo.thingmodel;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "thing_model_template")
public class ThingModelTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String templateKey; // 模板唯一标识

    @Column(nullable = false)
    private String name; // 模板名称

    @Column
    private String description; // 模板描述

    // 分类ID，指向产品分类表
    private String categoryId;

    @Enumerated(EnumType.STRING)
    private TemplateStatusEnum status;

    private String author;

    private String tags; // 标签，JSON格式存储

    @Column(columnDefinition = "TEXT")
    private String templateData;

    private Integer usageCount;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime publishTime;

    @Version
    private Integer version;
}
