package com.iot.demo.common;

import com.iot.demo.thingmodel.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class EnumConverterConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // 注册所有枚举的转换器
        registry.addConverter(new StringToOwnerTypeEnumConverter());
        registry.addConverter(new StringToDataTypeEnumConverter());
        registry.addConverter(new StringToAccessModeEnumConverter());
        registry.addConverter(new StringToDirectionEnumConverter());
        registry.addConverter(new StringToCallTypeEnumConverter());
    }

    // OwnerTypeEnum 转换器
    public static class StringToOwnerTypeEnumConverter implements Converter<String, OwnerTypeEnum> {
        @Override
        public OwnerTypeEnum convert(String source) {
            if (source == null || source.trim().isEmpty()) {
                return null;
            }
            return OwnerTypeEnum.fromValue(source.trim());
        }
    }

    // DataTypeEnum 转换器
    public static class StringToDataTypeEnumConverter implements Converter<String, DataTypeEnum> {
        @Override
        public DataTypeEnum convert(String source) {
            if (source == null || source.trim().isEmpty()) {
                return null;
            }
            return DataTypeEnum.fromValue(source.trim());
        }
    }

    // AccessModeEnum 转换器
    public static class StringToAccessModeEnumConverter implements Converter<String, AccessModeEnum> {
        @Override
        public AccessModeEnum convert(String source) {
            if (source == null || source.trim().isEmpty()) {
                return null;
            }
            return AccessModeEnum.fromValue(source.trim());
        }
    }

    // DirectionEnum 转换器
    public static class StringToDirectionEnumConverter implements Converter<String, DirectionEnum> {
        @Override
        public DirectionEnum convert(String source) {
            if (source == null || source.trim().isEmpty()) {
                return null;
            }
            return DirectionEnum.fromValue(source.trim());
        }
    }

    // CallTypeEnum 转换器
    public static class StringToCallTypeEnumConverter implements Converter<String, CallTypeEnum> {
        @Override
        public CallTypeEnum convert(String source) {
            if (source == null || source.trim().isEmpty()) {
                return null;
            }
            return CallTypeEnum.fromValue(source.trim());
        }
    }
}