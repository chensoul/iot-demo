package com.iot.demo.product;

import java.util.Objects;

public enum ValidateTypeEnum {
    NONE("免校验"),
    WEAK("弱校验");

    private String value;

    ValidateTypeEnum(String value) {
        this.value = value;
    }

    public static ValidateTypeEnum fromValue(String value) {
        for (ValidateTypeEnum t : values()) {
            if (Objects.equals(t.value, value)) return t;
        }
        throw new IllegalArgumentException("未知校验类型: " + value);
    }

    public String getValue() {
        return value;
    }
}
