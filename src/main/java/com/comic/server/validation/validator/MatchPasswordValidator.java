package com.comic.server.validation.validator;

import com.comic.server.common.payload.IMatchPassword;
import com.comic.server.validation.annotation.MatchPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class MatchPasswordValidator implements ConstraintValidator<MatchPassword, IMatchPassword> {

  private Boolean allowedEmpty;

  @Override
  public void initialize(MatchPassword constraintAnnotation) {
    allowedEmpty = constraintAnnotation.allowedEmpty();
  }

  @Override
  public boolean isValid(IMatchPassword request, ConstraintValidatorContext context) {
    String password = request.getPasswordToMatch();
    String confirmPassword = request.getMatchingPassword();

    if (allowedEmpty && !StringUtils.hasText(password) && !StringUtils.hasText(confirmPassword))
      return true;

    return password.equals(confirmPassword);
  }
}
