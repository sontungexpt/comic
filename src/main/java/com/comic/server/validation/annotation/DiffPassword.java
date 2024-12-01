package com.comic.server.validation.annotation;

import com.comic.server.validation.validator.DiffPasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DiffPasswordValidator.class)
@Documented
public @interface DiffPassword {
  String message() default "The new passwords must difference from confirm password.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
