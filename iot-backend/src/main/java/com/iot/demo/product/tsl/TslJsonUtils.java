package com.iot.demo.product.tsl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * 工具类：解析和校验阿里云TSL物模型JSON结构
 */
public class TslJsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper().configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);

    public static boolean validateTsl(String tslJson) {
        try {
            mapper.readValue(tslJson, TslDTO.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
