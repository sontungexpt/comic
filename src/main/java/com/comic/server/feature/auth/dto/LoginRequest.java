package com.comic.server.feature.auth.dto;

import com.comic.server.validation.annotation.Password;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
  private String username;

  @Password private String password;
}
