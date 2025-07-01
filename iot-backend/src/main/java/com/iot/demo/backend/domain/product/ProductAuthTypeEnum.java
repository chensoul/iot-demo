package com.iot.demo.backend.domain.product;

import java.util.Objects;

public enum ProductAuthTypeEnum {
    SECRET("设备密钥"),
    CERT("证书");

    private final String value;

    ProductAuthTypeEnum(String value) {
        this.value = value;
    }

    public static ProductAuthTypeEnum fromValue(String value) {
        for (ProductAuthTypeEnum t : values()) {
            if (Objects.equals(t.value, value)) return t;
        }
        throw new IllegalArgumentException("未知认证类型: " + value);
    }

    public String getValue() {
        return value;
    }
}
