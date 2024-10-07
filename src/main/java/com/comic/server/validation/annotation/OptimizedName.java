package com.comic.server.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.OverridesAttribute;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Documented
@NotBlank
@Size
public @interface OptimizedName {

  String message() default "Name must be between {min} and {max} characters";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  @OverridesAttribute(constraint = Size.class, name = "min")
  int min() default 3;

  @OverridesAttribute(constraint = Size.class, name = "max")
  int max() default 50;
}
