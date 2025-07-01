package com.iot.demo.product;

public enum ProductAuthTypeEnum {
    SECRET(0),
    CERT(1);

    private final int value;

    ProductAuthTypeEnum(int value) {
        this.value = value;
    }

    public static ProductAuthTypeEnum fromValue(int value) {
        for (ProductAuthTypeEnum t : values()) {
            if (t.value == value) return t;
        }
        throw new IllegalArgumentException("未知认证类型: " + value);
    }

    public int getValue() {
        return value;
    }
}
