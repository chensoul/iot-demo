package com.iot.demo.thingmodel;

public enum TemplateCategoryEnum {
    SENSOR("传感器"),
    ACTUATOR("执行器"),
    GATEWAY("网关"),
    CONTROLLER("控制器"),
    CAMERA("摄像头"),
    DISPLAY("显示屏"),
    SMART_HOME("智能家居"),
    INDUSTRIAL("工业设备"),
    AGRICULTURE("农业设备"),
    MEDICAL("医疗设备"),
    VEHICLE("车载设备"),
    WEARABLE("可穿戴设备"),
    OTHER("其他");

    private final String description;

    TemplateCategoryEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
