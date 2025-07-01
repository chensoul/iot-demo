package com.iot.demo.product.tsl;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TslDTO {
    private String schema;

    private TslProfileDTO profile;

    private List<TslPropertyDTO> properties;

    private List<TslServiceDTO> services;

    private List<TslEventDTO> events;

    @Data
    public static class TslProfileDTO {
        //        private String version;
        private String productKey;
    }

    @Data
    public static class TslPropertyDTO {
        private String identifier;
        private String name;
        private AccessModeEnum accessMode;
        private Boolean required;
        private TslDataTypeDTO dataType;
    }

    @Data
    public static class TslEventDTO {
        private String identifier;
        private String name;
        private String desc;
        private EventTypeEnum type;
        private Boolean required;
        private List<TslParameterDTO> outputData;
        private String method;
    }

    @Data
    public static class TslServiceDTO {
        private String identifier;
        private String name;
        private String desc;
        private Boolean required;
        private CallTypeEnum callType;
        private List<TslParameterDTO> inputData;
        private List<TslParameterDTO> outputData;
        private String method;
    }

    @Data
    public static class TslParameterDTO {
        private String identifier;
        private String name;
        private TslDataTypeDTO dataType;
    }

    @Data
    public static class TslDataTypeDTO {
        private DataTypeEnum type;
        private TslSpecDTO specs;
    }


    @Data
    public static class TslSpecDTO {
        private Double min; //参数最小值（int、float、double类型特有）。
        private Double max; //参数最大值（int、float、double类型特有）。
        private String unit; //属性单位（int、float、double类型特有，非必填）。
        private String unitName; //单位名称（int、float、double类型特有，非必填）。
        private Integer size; //数组元素的个数，最大512（array类型特有）。
        private Double step; //步长（text、enum类型无此参数）。
        private Integer length; //数据长度，最大10240（text类型特有）。
        private String zero; //0的值（bool类型特有）。
        private String one; // 1的值（bool类型特有）。
        private TslDataTypeDTO item; // array类型的元素类型
    }
}
