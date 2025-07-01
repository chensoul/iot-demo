package com.iot.demo.product.tsl;

import com.fasterxml.jackson.databind.EnumNamingStrategies;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 工具类：解析和校验阿里云TSL物模型JSON结构
 */
public class TslJsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper().setEnumNamingStrategy(EnumNamingStrategies.LowerCaseStrategy.INSTANCE);

    public static TslDTO parseTsl(String tslJson) {
        try {
            return mapper.readValue(tslJson, TslDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("TSL物模型格式不正确", e);
        }
    }
}
