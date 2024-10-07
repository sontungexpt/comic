package com.comic.server.utils;

import com.comic.server.exceptions.JwtTokenException;
import com.comic.server.feature.auth.enums.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtUtils {

  public static String extractSubject(String token, String secretKey) {
    return extractClaim(token, Claims::getSubject, secretKey);
  }

  public static Date extractExpiration(String token, String secretKey) {
    return extractClaim(token, Claims::getExpiration, secretKey);
  }

  public static <T> T extractClaim(
      String token, Function<Claims, T> claimsResolver, String secretKey) {
    final Claims claims = extractAllClaims(token, secretKey);
    return claimsResolver.apply(claims);
  }

  public static String buildToken(String subject, long expirationMsFromNow, String secretKey) {
    return buildToken(new HashMap<>(), subject, expirationMsFromNow, secretKey);
  }

  public static String buildToken(String subject, Date expiration, String secretKey) {
    return buildToken(new HashMap<>(), subject, expiration, secretKey);
  }

  public static String buildToken(
      Map<String, Object> extraClaims, String subject, long expirationMsFromNow, String secretKey) {
    return buildToken(
        extraClaims,
        subject,
        new Date(System.currentTimeMillis() + expirationMsFromNow),
        secretKey);
  }

  public static String buildToken(
      Map<String, Object> extraClaims, String subject, Date expiration, String secretKey) {
    return buildToken(extraClaims, subject, expiration, secretKey, Jwts.SIG.HS256);
  }

  public static String buildToken(
      Map<String, Object> extraClaims,
      String subject,
      Date expiration,
      String secretKey,
      MacAlgorithm alg) {
    return Jwts.builder()
        .claims(extraClaims)
        .subject(subject)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(expiration)
        .signWith(getSignInKey(secretKey), alg)
        .compact();
  }

  public static Claims extractAllClaims(String token, String secretKey) {
    try {
      return Jwts.parser()
          .verifyWith(getSignInKey(secretKey))
          .build()
          .parseSignedClaims(token)
          .getPayload();

    } catch (MalformedJwtException ex) {
      log.info("Malformed JWT token");
      throw new JwtTokenException(TokenType.BEARER, token, "Malformed jwt token");

    } catch (ExpiredJwtException ex) {
      log.info("JWT token expired");
      throw new JwtTokenException(TokenType.BEARER, token, "Token expired. Refresh required");

    } catch (UnsupportedJwtException ex) {
      log.info("Unsupported JWT token");
      throw new JwtTokenException(TokenType.BEARER, token, "Unsupported JWT token");

    } catch (IllegalArgumentException ex) {
      log.info("Illegal argument token");
      throw new JwtTokenException(TokenType.BEARER, token, "Illegal argument token");

    } catch (JwtException ex) {
      log.info("Invalid JWT token");
      throw new JwtTokenException(TokenType.BEARER, token, "Invalid JWT token");
    }
  }

  public static SecretKey getSignInKey(String secretKey) {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
