package com.comic.server.validation.validator;

import com.comic.server.common.payload.IDiffPassword;
import com.comic.server.validation.annotation.DiffPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DiffPasswordValidator implements ConstraintValidator<DiffPassword, IDiffPassword> {

  @Override
  public void initialize(DiffPassword constraintAnnotation) {}

  @Override
  public boolean isValid(IDiffPassword request, ConstraintValidatorContext context) {
    String password = request.getPasswordToDiff();
    String confirmPassword = request.getDiffTargetPassword();

    if (!StringUtils.hasText(password) && !StringUtils.hasText(confirmPassword)) return false;
    return !password.equals(confirmPassword);
  }
}
