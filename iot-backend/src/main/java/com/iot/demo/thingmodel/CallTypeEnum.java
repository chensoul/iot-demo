package com.iot.demo.thingmodel;

public enum CallTypeEnum {
    SYNC("sync"),
    ASYNC("async");

    private final String value;

    CallTypeEnum(String value) {
        this.value = value;
    }

    public static CallTypeEnum fromValue(String value) {
        for (CallTypeEnum c : values()) {
            if (c.value.equalsIgnoreCase(value)) return c;
        }
        throw new IllegalArgumentException("未知调用类型: " + value);
    }

    public String getValue() {
        return value;
    }
}
