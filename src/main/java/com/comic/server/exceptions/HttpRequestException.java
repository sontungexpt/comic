package com.comic.server.exceptions;

import lombok.Getter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

@Getter
public class HttpRequestException extends RuntimeException {

  protected HttpStatus responseStatus;
  protected HttpMethod method;

  public HttpRequestException(HttpMethod method, HttpStatus responseStatus, String message) {
    super(
        String.format(
            "Failed to request with method: %s response status: %s response: %s",
            method, responseStatus, message));
    this.method = method;
    this.responseStatus = responseStatus;
  }
}
