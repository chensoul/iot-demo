package com.iot.demo.backend.domain.product.tsl;

public enum EventTypeEnum {
    INFO("info"),
    ERROR("error"),
    ALERT("alert");

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
