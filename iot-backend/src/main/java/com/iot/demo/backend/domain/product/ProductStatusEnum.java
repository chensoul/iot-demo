package com.iot.demo.backend.domain.product;

public enum ProductStatusEnum {
    DEV(0),
    RELEASE(1);

    private final int value;

    ProductStatusEnum(int value) {
        this.value = value;
    }

    public static ProductStatusEnum fromValue(int value) {
        for (ProductStatusEnum t : values()) {
            if (t.value == value) return t;
        }
        throw new IllegalArgumentException("未知状态: " + value);
    }

    public int getValue() {
        return value;
    }
}
