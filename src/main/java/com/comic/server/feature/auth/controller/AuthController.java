package com.comic.server.feature.auth.controller;

import com.comic.server.annotation.BearerToken;
import com.comic.server.annotation.PublicEndpoint;
import com.comic.server.config.OpenApiConfig;
import com.comic.server.feature.auth.dto.LoginRequest;
import com.comic.server.feature.auth.dto.RegistrationRequest;
import com.comic.server.feature.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.ServletException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PublicEndpoint
@RequestMapping("/api/v1/auth")
public record AuthController(AuthService authService) {

  @Operation(summary = "Registers a new user to the system")
  @PostMapping("/register")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void registerUser(@RequestBody @Valid RegistrationRequest signUpRequest) {
    authService.register(signUpRequest);
  }

  @Operation(summary = "Logs the user in to the system and return the auth tokens")
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @PostMapping("/refresh-token")
  @Operation(summary = "Refresh the expired jwt authentication")
  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_NAME)
  public ResponseEntity<?> refreshToken(@BearerToken String refreshToken) throws ServletException {
    return ResponseEntity.ok(authService.refreshToken(refreshToken));
  }
}
