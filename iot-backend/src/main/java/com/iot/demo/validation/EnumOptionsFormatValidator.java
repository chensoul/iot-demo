package com.iot.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumOptionsFormatValidator implements ConstraintValidator<EnumOptionsFormat, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true; // 交由 @NotBlank 校验
        }
        String v = value.trim();
        // 允许 JSON 格式或 value:desc,value:desc 逗号分隔格式
        if ((v.startsWith("{") && v.endsWith("}")) || (v.startsWith("[") && v.endsWith("]"))) {
            return true;
        }
        // 允许 value:desc,value:desc
        String[] pairs = v.split(",");
        for (String pair : pairs) {
            if (!pair.contains(":")) {
                return false;
            }
            String[] kv = pair.split(":", 2);
            if (kv.length != 2 || kv[0].isEmpty() || kv[1].isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
