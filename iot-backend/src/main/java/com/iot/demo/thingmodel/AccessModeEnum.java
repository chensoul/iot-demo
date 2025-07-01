package com.iot.demo.thingmodel;

public enum AccessModeEnum {
    R("r"),
    W("w"),
    RW("rw");

    private final String value;

    AccessModeEnum(String value) {
        this.value = value;
    }

    public static AccessModeEnum fromValue(String value) {
        for (AccessModeEnum m : values()) {
            if (m.value.equalsIgnoreCase(value)) return m;
        }
        throw new IllegalArgumentException("未知的读写类型: " + value);
    }

    public String getValue() {
        return value;
    }
}
