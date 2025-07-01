package com.iot.demo.backend.domain.product;

public enum ProductNodeTypeEnum {
    DIRECT(0),      // 直连设备
    GATEWAY(1),     // 网关设备
    GATEWAY_SUB(2);         // 网关子设备

    private final int value;

    ProductNodeTypeEnum(int value) {
        this.value = value;
    }

    public static ProductNodeTypeEnum fromValue(int value) {
        for (ProductNodeTypeEnum t : values()) {
            if (t.value == value) return t;
        }
        throw new IllegalArgumentException("未知节点类型: " + value);
    }

    public int getValue() {
        return value;
    }
}
