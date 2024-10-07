package com.comic.server.exceptions.advice;

import com.comic.server.common.payload.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.BadRequestException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ValidationExceptionAdvice {

  // @ExceptionHandler(MethodArgumentNotValidException.class)
  // @ResponseStatus(HttpStatus.BAD_REQUEST)
  // public ExceptionResponse handleHandlerMethodValidationException(
  //     MethodArgumentNotValidException e, HttpServletRequest request) {
  //   final Map<String, String> body = new HashMap<>();
  //   e.getFieldErrors
  //       .forEach(
  //           objectError -> {
  //             log.info("Object error: {}", objectError);
  //             final String[] fieldError = objectError.getCodes()[0].split("\\.");
  //             body.put(fieldError[fieldError.length - 1], objectError.getDefaultMessage());
  //           });
  //   return new ExceptionResponse(e, HttpStatus.BAD_REQUEST, body, request);
  // }
  //
  //
  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<?> handleValidationException(
      ValidationException e, HttpServletRequest request) {
    return ExceptionResponse.builder(e, HttpStatus.BAD_REQUEST)
        .request(request)
        .build()
        .toResponseEntity();
  }

  @ExceptionHandler(BindException.class)
  public ResponseEntity<?> handleBindException(BindException e, HttpServletRequest request) {
    final Map<String, String> body = new HashMap<>();
    e.getFieldErrors()
        .forEach(fieldError -> body.put(fieldError.getField(), fieldError.getDefaultMessage()));

    return ExceptionResponse.builder(e, HttpStatus.BAD_REQUEST)
        .data(body)
        .request(request)
        .build()
        .toResponseEntity();
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<?> handleHandlerMethodValidationException(
      HandlerMethodValidationException e, HttpServletRequest request) {
    final Map<String, String> body = new HashMap<>();
    e.getAllErrors()
        .forEach(
            objectError -> {
              final String[] fieldError = objectError.getCodes()[0].split("\\.");
              body.put(fieldError[fieldError.length - 1], objectError.getDefaultMessage());
            });
    return ExceptionResponse.builder(e, HttpStatus.BAD_REQUEST)
        .data(body)
        .request(request)
        .build()
        .toResponseEntity();
  }

  @ExceptionHandler(IllegalAccessException.class)
  public ResponseEntity<?> handleIllegalArgumentException(
      IllegalAccessException e, HttpServletRequest request) {
    return ExceptionResponse.builder(e, HttpStatus.BAD_REQUEST)
        .request(request)
        .build()
        .toResponseEntity();
  }

  @ExceptionHandler({BadRequestException.class})
  public ResponseEntity<?> handleBadRequestException(
      BadRequestException ex, HttpServletRequest request) {
    return ExceptionResponse.builder(ex, HttpStatus.BAD_REQUEST)
        .request(request)
        .build()
        .toResponseEntity();
  }
}
