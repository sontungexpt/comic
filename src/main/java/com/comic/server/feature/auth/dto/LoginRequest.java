package com.comic.server.feature.auth.dto;

import com.comic.server.validation.annotation.Password;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

  @Schema(description = "Username", example = "admin")
  private String username;

  @Schema(description = "Password", example = "Admin123")
  @Password
  private String password;
}
