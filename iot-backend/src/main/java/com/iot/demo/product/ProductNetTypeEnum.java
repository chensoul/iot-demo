package com.iot.demo.product;

public enum ProductNetTypeEnum {
    WIFI("Wi-Fi"),
    CELLULAR("蜂窝（2G/3G/4G/5G）"),
    ETHERNET("以太网"),
    OTHER("其他");

    private final String value;

    ProductNetTypeEnum(String value) {
        this.value = value;
    }

    public static ProductNetTypeEnum fromValue(String value) {
        for (ProductNetTypeEnum d : values()) {
            if (d.value.equalsIgnoreCase(value)) return d;
        }
        throw new IllegalArgumentException("未知的方向: " + value);
    }

    public String getValue() {
        return value;
    }
}
