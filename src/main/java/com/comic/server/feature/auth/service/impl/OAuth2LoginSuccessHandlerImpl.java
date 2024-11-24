package com.comic.server.feature.auth.service.impl;

import com.comic.server.utils.ConsoleUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class OAuth2LoginSuccessHandlerImpl implements AuthenticationSuccessHandler {

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {
    ConsoleUtils.prettyPrint(authentication.getPrincipal());
    // response.setStatus(HttpStatus.OK.value());
    // String body = new ObjectMapper().writeValueAsString(authentication.getPrincipal());

    // response.setContentLength(body.length());
    // response.getWriter().write(body);
    // response.setContentType("application/json");
  }
}
