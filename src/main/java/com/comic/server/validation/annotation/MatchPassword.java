package com.comic.server.validation.annotation;

import com.comic.server.validation.validator.MatchPasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MatchPasswordValidator.class)
@Documented
public @interface MatchPassword {
  String message() default "The new passwords must match with confirm password.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  // The password can be empty (no need password, user can leave it blank)
  boolean allowedEmpty() default false;
}
