package com.comic.server.exceptions.advice;

import com.comic.server.common.payload.ExceptionResponse;
import com.comic.server.exceptions.ResourceAlreadyInUseException;
import com.comic.server.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ResourceExceptionAdvice {

  @ExceptionHandler({ResourceAlreadyInUseException.class})
  @ResponseStatus(HttpStatus.CONFLICT)
  public ExceptionResponse handleResourceAlreadyInUseException(
      ResourceAlreadyInUseException ex, HttpServletRequest request) {
    return ExceptionResponse.builder(ex).request(request).build();
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ExceptionResponse handleResourceNotFoundException(
      ResourceNotFoundException ex, HttpServletRequest request) {
    return ExceptionResponse.builder(ex).request(request).build();
  }
}
