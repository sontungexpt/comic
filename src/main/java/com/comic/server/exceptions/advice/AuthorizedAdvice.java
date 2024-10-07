package com.comic.server.exceptions.advice;

import com.comic.server.common.payload.ExceptionResponse;
import com.comic.server.exceptions.JwtTokenException;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthorizedAdvice {

  @ExceptionHandler({
    UsernameNotFoundException.class,
    BadCredentialsException.class,
  })
  public ResponseEntity<?> handleUserLoginException(Exception ex, HttpServletRequest request) {
    return ExceptionResponse.builder(ex, HttpStatus.NOT_FOUND)
        .message("Password or username is incorrect")
        .request(request)
        .build()
        .toResponseEntity();
  }

  @ExceptionHandler({AccessDeniedException.class, LockedException.class, DisabledException.class})
  public ResponseEntity<?> handleAccessDeniedException(Exception ex, HttpServletRequest request) {
    return ExceptionResponse.builder(ex, HttpStatus.FORBIDDEN)
        .request(request)
        .build()
        .toResponseEntity();
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<?> handleAuthenticationException(
      AuthenticationException ex, HttpServletRequest request) {
    return ExceptionResponse.builder(ex, HttpStatus.UNAUTHORIZED)
        .request(request)
        .build()
        .toResponseEntity();
  }

  @ExceptionHandler(JwtTokenException.class)
  public ResponseEntity<?> handleInvalidTokenException(
      JwtTokenException ex, HttpServletRequest request) {
    return ExceptionResponse.builder(ex, HttpStatus.UNAUTHORIZED)
        .request(request)
        .build()
        .toResponseEntity();
  }
}
