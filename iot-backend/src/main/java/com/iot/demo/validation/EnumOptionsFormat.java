package com.iot.demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EnumOptionsFormatValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumOptionsFormat {
    String message() default "枚举值格式非法，应为 value:desc,value:desc 或 JSON 格式";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
