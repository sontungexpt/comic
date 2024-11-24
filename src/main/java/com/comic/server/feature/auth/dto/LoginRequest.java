package com.comic.server.feature.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

  @Schema(description = "Username", example = "admin")
  @NotBlank
  private String username;

  @Schema(description = "Password", example = "Admin123")
  @NotBlank
  private String password;
}
