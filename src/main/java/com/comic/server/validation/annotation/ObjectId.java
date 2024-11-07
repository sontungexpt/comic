package com.comic.server.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target({
  ElementType.TYPE,
  ElementType.TYPE_USE,
  ElementType.PARAMETER,
  ElementType.FIELD,
  ElementType.ANNOTATION_TYPE,
})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Valid
@Pattern(regexp = "^[0-9a-fA-F]{24}$")
public @interface ObjectId {

  @AliasFor(annotation = Pattern.class, attribute = "message")
  String message() default "Value must be a valid ObjectId";

  @AliasFor(annotation = Pattern.class, attribute = "groups")
  Class<?>[] groups() default {};

  @AliasFor(annotation = Pattern.class, attribute = "payload")
  Class<? extends Payload>[] payload() default {};
}
