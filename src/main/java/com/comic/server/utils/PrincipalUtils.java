package com.comic.server.utils;

import com.comic.server.exceptions.AuthenticationException;
import com.comic.server.feature.user.model.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class PrincipalUtils {

  public static final User getUser() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof User) return (User) principal;
    throw new AuthenticationException();
  }

  public static final String getUserId() {
    return getUser().getId();
  }

  public static final Object getPrincipal() {
    return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  public static final String getUsername() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }
}
