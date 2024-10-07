package com.comic.server.feature.auth.service.impl;

import com.comic.server.exceptions.JwtTokenException;
import com.comic.server.exceptions.ResourceAlreadyInUseException;
import com.comic.server.feature.auth.dto.JwtResponse;
import com.comic.server.feature.auth.dto.LoginRequest;
import com.comic.server.feature.auth.dto.LoginResponse;
import com.comic.server.feature.auth.dto.RegistrationRequest;
import com.comic.server.feature.auth.jwt.JwtService;
import com.comic.server.feature.auth.model.RefreshToken;
import com.comic.server.feature.auth.repository.RefreshTokenRepository;
import com.comic.server.feature.auth.service.AuthService;
import com.comic.server.feature.user.model.User;
import com.comic.server.feature.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public record AuthServiceImpl(
    RefreshTokenRepository refreshTokenRepository,
    JwtService jwtService,
    UserService userService,
    AuthenticationManager authenticationManager)
    implements AuthService {

  @Override
  public LoginResponse login(LoginRequest loginRequest) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()));

    User user = (User) authentication.getPrincipal();
    // if (user == null)
    //   throw new UsernameNotFoundException(
    //       "User not found with phone number " + loginRequest.getPhoneNumber());

    SecurityContextHolder.getContext().setAuthentication(authentication);

    final String accessToken = jwtService.generateAccessToken(user);
    final RefreshToken refreshToken = jwtService.generateRefreshToken(user);

    refreshTokenRepository.save(refreshToken);

    log.info("Account with public id {} logged in successfully", user.getPubId());

    return LoginResponse.builder()
        .name(user.getName())
        .jwt(new JwtResponse(accessToken, refreshToken.getToken()))
        .build();
  }

  @Override
  @Transactional
  public void register(RegistrationRequest request) {
    final String username = request.getUsername();

    if (userService.existsByUsername(username)) {
      throw new ResourceAlreadyInUseException(User.class, "username", username);
    }

    userService.createUser(username, request.getPassword(), request.getName());
  }

  @Override
  @Transactional
  public JwtResponse refreshToken(String refreshToken) {
    RefreshToken savedRefreshToken =
        refreshTokenRepository
            .findByToken(refreshToken)
            .orElseThrow(
                () -> {
                  log.warn("Refresh token {} not found", refreshToken);
                  return new JwtTokenException(refreshToken, "Refresh token not found");
                });

    if (savedRefreshToken.isRevoked()) {
      throw new JwtTokenException(refreshToken, "Refresh token revoked");
    } else if (savedRefreshToken.isExpired()) {
      refreshTokenRepository.save(savedRefreshToken.revoke());
      throw new JwtTokenException(refreshToken, "Refresh token expired");
    }

    String newAccessToken = jwtService.generateAccessToken(savedRefreshToken.getUserPubId());
    RefreshToken updatedRefreshToken = refreshTokenRepository.save(savedRefreshToken.refresh());

    log.info(
        "Refresh token {} refreshed successfully for user with public id {}",
        refreshToken,
        savedRefreshToken.getUserPubId());

    return new JwtResponse(newAccessToken, updatedRefreshToken.getToken());
  }
}
