package com.iot.demo.thingmodel;

public enum OwnerTypeEnum {
    SERVICE("service"),
    EVENT("event");

    private final String value;

    OwnerTypeEnum(String value) {
        this.value = value;
    }

    public static OwnerTypeEnum fromValue(String value) {
        for (OwnerTypeEnum o : values()) {
            if (o.value.equalsIgnoreCase(value)) return o;
        }
        throw new IllegalArgumentException("未知的归属类型: " + value);
    }

    public String getValue() {
        return value;
    }
}
