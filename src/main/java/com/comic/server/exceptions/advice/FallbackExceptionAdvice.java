package com.comic.server.exceptions.advice;

import com.comic.server.common.payload.ExceptionResponse;
import com.comic.server.exceptions.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class FallbackExceptionAdvice {

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<?> handleUnwantedHttpException(
      BaseException e, HttpServletRequest request) {
    return ExceptionResponse.builder(e).request(request).build().toResponseEntity();
  }

  @ExceptionHandler(MissingServletRequestPartException.class)
  public ResponseEntity<?> handleMissingServletRequestPartException(
      MissingServletRequestPartException e, HttpServletRequest request) {
    return ExceptionResponse.builder(e, HttpStatus.FORBIDDEN)
        .request(request)
        .build()
        .toResponseEntity();
  }

  @ExceptionHandler(HttpClientErrorException.class)
  public ResponseEntity<?> handleHttpClientErrorException(
      HttpClientErrorException e, HttpServletRequest request) {
    return ExceptionResponse.builder(e, HttpStatus.valueOf(e.getStatusCode().value()))
        .request(request)
        .build()
        .toResponseEntity();
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<?> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException e, HttpServletRequest request) {
    return ExceptionResponse.builder(e, HttpStatus.UNPROCESSABLE_ENTITY)
        .request(request)
        .build()
        .toResponseEntity();
  }

  @ExceptionHandler(UnsupportedOperationException.class)
  public ResponseEntity<?> handleUnsuuportOperationException(
      UnsupportedOperationException e, HttpServletRequest request) {
    return ExceptionResponse.builder(e, HttpStatus.EXPECTATION_FAILED)
        .request(request)
        .build()
        .toResponseEntity();
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleUnwantedException(Exception e, HttpServletRequest request) {
    log.error(e.getClass().getSimpleName() + ": " + e.getMessage());
    return ExceptionResponse.builder(e, HttpStatus.INTERNAL_SERVER_ERROR)
        .request(request)
        .build()
        .toResponseEntity();
  }
}
