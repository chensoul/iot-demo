package com.iot.demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NameFormatValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NameFormat {
    String message() default "名称格式非法：仅支持中文、英文字母、日文、数字、_、-、@、()，长度4~30（中/日文算2个字符）";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
