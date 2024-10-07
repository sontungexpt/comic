package com.comic.server.utils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class HttpServletRequestUtils {

  public static Optional<HttpServletRequest> getRequest() {
    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
    return Optional.ofNullable((ServletRequestAttributes) attributes)
        .map(ServletRequestAttributes::getRequest);
  }
}
