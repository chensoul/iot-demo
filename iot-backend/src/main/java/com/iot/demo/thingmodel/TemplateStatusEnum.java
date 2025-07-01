package com.iot.demo.thingmodel;

public enum TemplateStatusEnum {
    DRAFT("草稿"),
    PUBLISHED("已发布"),
    DEPRECATED("已废弃");

    private final String description;

    TemplateStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
