package com.comic.server.exceptions;

import org.springframework.http.HttpStatus;

public class TooManyRequestsException extends BaseException {

  public TooManyRequestsException(String message) {
    super(HttpStatus.TOO_MANY_REQUESTS, message);
  }

  public TooManyRequestsException(String message, Throwable cause) {
    super(HttpStatus.TOO_MANY_REQUESTS, message, cause);
  }
}
