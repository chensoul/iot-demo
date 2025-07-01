package com.iot.demo.backend.domain.product;

public enum TemplateStatusEnum {
    DRAFT("草稿"),
    RELEASE("已发布");

    private final String description;

    TemplateStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
