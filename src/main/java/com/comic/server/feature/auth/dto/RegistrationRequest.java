package com.comic.server.feature.auth.dto;

import com.comic.server.validation.annotation.OptimizedName;
import com.comic.server.validation.annotation.Password;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {

  private String username;

  @Password private String password;

  @OptimizedName private String name;
}
