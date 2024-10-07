package com.comic.server.validation.validator;

import com.comic.server.validation.annotation.List;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import org.springframework.stereotype.Component;

@Component
public class ListValidator implements ConstraintValidator<List, Object> {

  private String[] validValues;

  @Override
  public void initialize(List constraintAnnotation) {
    this.validValues = constraintAnnotation.value();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    if (validValues.length == 0 || value == null) {
      return false;
    } else if (value instanceof String) {
      return Arrays.asList(validValues).contains(value);
    } else if (value instanceof Integer) {
      return Arrays.stream(validValues)
          .anyMatch(validValue -> Integer.parseInt(validValue) == (Integer) value);
    } else if (value instanceof Long) {
      return Arrays.stream(validValues)
          .anyMatch(validValue -> Long.parseLong(validValue) == (Long) value);
    } else if (value instanceof Double) {
      return Arrays.stream(validValues)
          .anyMatch(validValue -> Double.parseDouble(validValue) == (Double) value);
    } else if (value instanceof Float) {
      return Arrays.stream(validValues)
          .anyMatch(validValue -> Float.parseFloat(validValue) == (Float) value);
    } else if (value instanceof Boolean) {
      return Arrays.stream(validValues)
          .anyMatch(validValue -> Boolean.parseBoolean(validValue) == (Boolean) value);
    } else if (value instanceof Byte) {
      return Arrays.stream(validValues)
          .anyMatch(validValue -> Byte.parseByte(validValue) == (Byte) value);
    } else if (value instanceof Short) {
      return Arrays.stream(validValues)
          .anyMatch(validValue -> Short.parseShort(validValue) == (Short) value);
    } else if (value instanceof Character) {
      return Arrays.stream(validValues)
          .anyMatch(validValue -> validValue.charAt(0) == (Character) value);
    } else if (value instanceof Enum) {
      return Arrays.stream(validValues)
          .anyMatch(validValue -> validValue.equals(((Enum<?>) value).name()));
    }
    return false;
  }
}
