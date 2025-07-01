package com.iot.demo.thingmodel;

public enum EventTypeEnum {
    INFO("info"),
    WARNING("warning"),
    ALARM("alarm");

    private final String value;

    EventTypeEnum(String value) {
        this.value = value;
    }

    public static EventTypeEnum fromValue(String value) {
        for (EventTypeEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("未知事件类型: " + value);
    }

    public String getValue() {
        return value;
    }
}
