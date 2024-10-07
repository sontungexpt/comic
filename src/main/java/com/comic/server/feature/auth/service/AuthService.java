package com.comic.server.feature.auth.service;

import com.comic.server.feature.auth.dto.JwtResponse;
import com.comic.server.feature.auth.dto.LoginRequest;
import com.comic.server.feature.auth.dto.LoginResponse;
import com.comic.server.feature.auth.dto.RegistrationRequest;

public interface AuthService {

  LoginResponse login(LoginRequest loginRequest);

  void register(RegistrationRequest request);

  JwtResponse refreshToken(String refreshToken);
}
