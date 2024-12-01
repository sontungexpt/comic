package com.comic.server.feature.auth.service.impl;

import com.comic.server.exceptions.JwtTokenException;
import com.comic.server.feature.auth.dto.JwtResponse;
import com.comic.server.feature.auth.jwt.JwtService;
import com.comic.server.feature.auth.model.RefreshToken;
import com.comic.server.feature.auth.repository.RefreshTokenRepository;
import com.comic.server.feature.auth.service.RefreshTokenService;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtService jwtService;

  @Override
  public RefreshToken validateRefreshToken(RefreshToken refreshToken) {
    return validateRefreshToken(refreshToken, (rt) -> {});
  }

  @Override
  public RefreshToken validateRefreshToken(
      RefreshToken refreshToken, Consumer<RefreshToken> instrusionHandler) {
    if (refreshToken.isRevoked()) {
      instrusionHandler.accept(refreshToken);
      throw new JwtTokenException(refreshToken.getToken(), "Refresh token revoked");
    } else if (refreshToken.isExpired()) {
      refreshToken.revoke(refreshTokenRepository);
      throw new JwtTokenException(refreshToken.getToken(), "Refresh token expired");
    }
    return refreshToken;
  }

  @Override
  public RefreshToken getRefreshToken(String token) {
    return refreshTokenRepository
        .findByToken(token)
        .orElseThrow(
            () -> {
              log.warn("Refresh token {} not found", token);
              return new JwtTokenException(token, "Refresh token not found");
            });
  }

  @Override
  public RefreshToken getAndValidateRefreshToken(String token) {
    return validateRefreshToken(getRefreshToken(token));
  }

  @Override
  public RefreshToken getAndValidateRefreshToken(
      String token, Consumer<RefreshToken> instrusionHandler) {
    return validateRefreshToken(getRefreshToken(token), instrusionHandler);
  }

  @Override
  @Transactional
  public JwtResponse refreshJwtTokens(RefreshToken refreshToken) {
    String userPubId = refreshToken.getUserPubId();
    String newAccessToken = jwtService.generateAccessToken(userPubId);

    RefreshToken newRefreshToken = refreshToken.refresh(refreshTokenRepository);

    log.info(
        "Refresh token {} refreshed successfully for user with public id {}",
        refreshToken,
        refreshToken.getUserPubId());

    return new JwtResponse(newAccessToken, newRefreshToken.getToken());
  }

  @Override
  public RefreshToken generateRefreshToken(String userPubId) {
    return jwtService.generateRefreshTokenAndSaveToDB(userPubId);
  }
}
