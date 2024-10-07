package com.comic.server.exceptions;

public class AuthenticationException
    extends org.springframework.security.core.AuthenticationException {

  public AuthenticationException() {
    super("User is not authenticated");
  }

  public AuthenticationException(String msg) {
    super(msg);
  }
}
