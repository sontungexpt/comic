package com.comic.server.common.payload;

import com.comic.server.validation.annotation.MatchPassword;
import jakarta.validation.Valid;

@Valid
@MatchPassword
public interface IMatchPassword {

  /** Gets the password that needs to be matched. */
  String getPasswordToMatch();

  /** Gets the password that must match. */
  String getMatchingPassword();
}
