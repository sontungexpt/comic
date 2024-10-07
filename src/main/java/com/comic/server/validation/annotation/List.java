package com.comic.server.validation.annotation;

import com.comic.server.validation.validator.ListValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ListValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface List {
  String message() default "Giá trị không hợp lệ"; // Thông điệp mặc định

  Class<?>[] groups() default {}; // Nhóm

  Class<? extends Payload>[] payload() default {}; // Payload

  String[] value();
}
