package com.iot.demo.product;

public enum ProductDataFormatEnum {
    CUSTOM(0),
    JSON(1);

    private final int value;

    ProductDataFormatEnum(int value) {
        this.value = value;
    }

    public static ProductDataFormatEnum fromValue(int value) {
        for (ProductDataFormatEnum t : values()) {
            if (t.value == value) return t;
        }
        throw new IllegalArgumentException("未知数据格式: " + value);
    }

    public int getValue() {
        return value;
    }
}
