package com.iot.demo.thingmodel;

public enum DataTypeEnum {
    INT("int"),
    FLOAT("float"),
    DOUBLE("double"),
    STRING("string"),
    BOOL("bool"),
    ENUM("enum"),
    DATE("date"),
    STRUCT("struct"),
    ARRAY("array"),
    BINARY("binary");

    private final String value;

    DataTypeEnum(String value) {
        this.value = value;
    }

    public static DataTypeEnum fromValue(String value) {
        for (DataTypeEnum type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的数据类型: " + value);
    }

    public String getValue() {
        return value;
    }
}
