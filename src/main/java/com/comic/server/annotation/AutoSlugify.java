package com.comic.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotation to automatically generate a slug from a list of fields. */
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
  Separator separator() default Separator.HYPHEN;

  /** The slug must be unique or not. */
  boolean unique() default true;

  /** The strategy to update the slug. */
  UpdateStrategy updateStrategy() default UpdateStrategy.ON_VALUE_CHANGE;

  public enum UpdateStrategy {
    ON_DOCUMENT_SAVE,
    ON_VALUE_CHANGE,
    NEVER_UPDATE,
  }

  public enum Separator {
    UNDERSCORE("_"),
    HYPHEN("-"),
    ;

    private String value;

    Separator(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    public static Separator fromValue(String value) {
      for (Separator s : values()) {
        if (s.value.equals(value)) {
          return s;
        }
      }
      throw new IllegalArgumentException("No separator with value " + value);
    }

    @Override
    public String toString() {
      return value;
    }
  }
}
