package com.comic.server.feature.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResponse {
  private String name;

  private JwtResponse jwt;
}
