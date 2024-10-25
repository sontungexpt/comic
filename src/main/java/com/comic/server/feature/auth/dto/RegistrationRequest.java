package com.comic.server.feature.auth.dto;

import com.comic.server.validation.annotation.OptimizedName;
import com.comic.server.validation.annotation.Password;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {

  @Schema(description = "Username", example = "admin")
  private String username;

  @Schema(description = "Password", example = "Admin123")
  @Password
  private String password;

  @Schema(description = "Email", example = "Admin")
  @OptimizedName
  private String name;
}
