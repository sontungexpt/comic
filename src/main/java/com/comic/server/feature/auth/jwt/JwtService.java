package com.comic.server.feature.auth.jwt;

import com.comic.server.feature.auth.model.RefreshToken;
import com.comic.server.feature.auth.repository.RefreshTokenRepository;
import com.comic.server.feature.user.model.User;
import com.comic.server.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

  @Value("${jwt.secret-key}")
  private String JWT_SECRET_KEY;

  @Value("${jwt.access-token-expiration}")
  private long ACCESS_TOKEN_EXPIRATION;

  @Value("${jwt.refresh-token-expiration}")
  private long REFRESH_TOKEN_EXPIRATION;

  private final RefreshTokenRepository refreshTokenRepository;

  public String generateAccessToken(User user) {
    return buildToken(user.getPubId(), ACCESS_TOKEN_EXPIRATION);
  }

  public String generateAccessToken(String subject) {
    return buildToken(subject, ACCESS_TOKEN_EXPIRATION);
  }

  public boolean isAccessTokenValid(String token, User user) {
    return isTokenValid(token, user.getPubId());
  }

  public RefreshToken generateRefreshToken(User user) {
    return generateRefreshToken(user.getPubId());
  }

  public RefreshToken generateRefreshToken(String userPubId) {
    return new RefreshToken(userPubId, REFRESH_TOKEN_EXPIRATION);
  }

  public RefreshToken generateRefreshTokenAndSaveToDB(User user) {
    return generateRefreshTokenAndSaveToDB(user.getPubId());
  }

  public RefreshToken generateRefreshTokenAndSaveToDB(String userPubId) {
    RefreshToken refreshToken = new RefreshToken(userPubId, REFRESH_TOKEN_EXPIRATION);
    refreshTokenRepository.save(refreshToken);
    return refreshToken;
  }

  public boolean isTokenValid(String token, String subject) {
    return subject.equals(extractSubject(token)) && !isTokenExpired(token);
  }

  public String extractSubject(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public String buildToken(String subject, long expirationMsFromNow) {
    return buildToken(new HashMap<>(), subject, expirationMsFromNow);
  }

  public String buildToken(String subject, Date expiration) {
    return buildToken(new HashMap<>(), subject, expiration);
  }

  public String buildToken(
      Map<String, Object> extraClaims, String subject, long expirationMsFromNow) {
    return JwtUtils.buildToken(extraClaims, subject, expirationMsFromNow, JWT_SECRET_KEY);
  }

  public String buildToken(Map<String, Object> extraClaims, String subject, Date expiration) {
    return JwtUtils.buildToken(extraClaims, subject, expiration, JWT_SECRET_KEY);
  }

  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Claims extractAllClaims(String token) {
    return JwtUtils.extractAllClaims(token, JWT_SECRET_KEY);
  }
}
