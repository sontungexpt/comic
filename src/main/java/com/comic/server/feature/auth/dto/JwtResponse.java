package com.comic.server.feature.auth.dto;

import com.comic.server.feature.auth.enums.TokenType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {

  private String accessToken;

  private String refreshToken;

  private TokenType tokenType = TokenType.BEARER;

  public JwtResponse(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }
}
