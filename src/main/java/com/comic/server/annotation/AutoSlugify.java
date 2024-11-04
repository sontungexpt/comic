package com.comic.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoSlugify {

  /** The list of fields name from which the slug will be generated. */
  String[] fields();

  /**
   * If the slug is generated from a unique field, the slug will be generated from the field value
   * faster.
   */
  boolean fromUniqueField() default false;

  /** The separator between words in the slug. */
  boolean underscoreSeperation() default false;

  /** The slug must be unique or not. */
  boolean unique() default true;
}
