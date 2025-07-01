package com.iot.demo.thingmodel;

public enum DirectionEnum {
    IN("in"),
    OUT("out");

    private final String value;

    DirectionEnum(String value) {
        this.value = value;
    }

    public static DirectionEnum fromValue(String value) {
        for (DirectionEnum d : values()) {
            if (d.value.equalsIgnoreCase(value)) return d;
        }
        throw new IllegalArgumentException("未知的方向: " + value);
    }

    public String getValue() {
        return value;
    }
}
