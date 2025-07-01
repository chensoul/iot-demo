package com.iot.demo.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class NameFormatValidator implements ConstraintValidator<NameFormat, String> {
    private static final Pattern PATTERN = Pattern.compile("^[\\p{IsHan}\\p{IsHiragana}\\p{IsKatakana}A-Za-z0-9_\\-@()]+$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true; // 交由 @NotBlank 校验
        }
        String v = value.trim();
        if (!PATTERN.matcher(v).matches()) {
            return false;
        }
        int len = 0;
        for (int i = 0; i < v.length(); i++) {
            char c = v.charAt(i);
            // 中文或日文字符算2个长度
            if ((c >= 0x4e00 && c <= 0x9fa5) || (c >= 0x3040 && c <= 0x30ff)) {
                len += 2;
            } else {
                len += 1;
            }
        }
        return len >= 4 && len <= 30;
    }
}
