package com.comic.server.logging;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LoggingRequestFilter implements Filter {

  private final LoggingHttpRequestService loggingService;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    if (request.getContentLength() == 0) {
      loggingService.logRequestInfo((HttpServletRequest) request);
    }

    chain.doFilter(request, response);
  }
}
