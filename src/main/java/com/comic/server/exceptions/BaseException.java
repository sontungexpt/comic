package com.comic.server.exceptions;

import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException {

  protected HttpStatus status;

  public BaseException(HttpStatus status, String message) {
    super(message);
    this.status = status;
  }

  public BaseException(HttpStatus status, String message, Throwable cause) {
    super(message, cause);
    this.status = status;
  }

  public BaseException(Exception ex, HttpStatus status) {
    this(status, ex.getMessage(), ex.getCause());
  }

  public HttpStatus getStatus() {
    return status;
  }

  public int getStatusCode() {
    return status.value();
  }
}
