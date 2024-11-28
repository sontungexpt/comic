package com.comic.server.utils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.StringTokenizer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientIpAddressUtils {

  public static String getClientIpAddress(HttpServletRequest request) {
    String xForwardedForHeader = request.getHeader("X-Forwarded-For");
    if (xForwardedForHeader == null) {
      log.debug("X-Forwarded-For header is missing");
      return request.getRemoteAddr();
    } else {
      // As of https://en.wikipedia.org/wiki/X-Forwarded-For
      // The general format of the field is: X-Forwarded-For: client, proxy1, proxy2 ...
      // we only want the client
      log.debug("X-Forwarded-For header: {}", xForwardedForHeader);
      return new StringTokenizer(xForwardedForHeader, ",").nextToken().trim();
    }
  }
}
