package com.comic.server.validation.validator;

import com.comic.server.validation.annotation.Enum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class EnumValidator implements ConstraintValidator<Enum, String> {
  private java.lang.Enum<?>[] enumConstants;
  private boolean optional;

  @Override
  public void initialize(Enum annotation) {
    enumConstants = annotation.value().getEnumConstants();
    optional = annotation.optional();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    System.out.println("EnumValidator.isValid");
    if (optional && (value == null || value.isEmpty())) {
      return true;
    }
    for (var enumConstant : enumConstants) {
      if (enumConstant.name().equals(value)) {
        return true; // Valid value
      }
    }

    // Optionally set a custom message
    context.disableDefaultConstraintViolation();
    context
        .buildConstraintViolationWithTemplate(
            "Invalid value: '" + value + "'. Accepted values are: " + getAcceptedValues())
        .addConstraintViolation();
    return false;
  }

  private String getAcceptedValues() {
    StringBuilder sb = new StringBuilder();
    for (java.lang.Enum<?> enumConstant : enumConstants) {
      sb.append(enumConstant.name()).append(", ");
    }
    return sb.substring(0, sb.length() - 2); // Remove the last comma and space
  }
}
