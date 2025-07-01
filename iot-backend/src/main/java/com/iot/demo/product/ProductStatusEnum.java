package com.iot.demo.product;

public enum ProductStatusEnum {
    DISABLED(0),
    ENABLED(1);

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
