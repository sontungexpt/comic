package com.comic.server.validation.validator;

import com.comic.server.validation.annotation.Password;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PasswordValidator implements ConstraintValidator<Password, String> {

  String regexp;

  @Override
  public void initialize(Password constraintAnnotation) {
    this.regexp = constraintAnnotation.regexp();
  }

  @Override
  public boolean isValid(String string, ConstraintValidatorContext context) {
    if (!StringUtils.hasText(string)) return false;
    else if (string.matches(regexp)) return true;
    return false;
  }
}
