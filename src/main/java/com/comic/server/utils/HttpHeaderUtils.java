package com.comic.server.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@Slf4j
public final class HttpHeaderUtils {
  public static final String AUTHORIZATION_BEARER_PREFIX = "Bearer ";
  public static final String AUTHORIZATION_HEADER = "Authorization";

  /**
   * Get the value of the Authorization Bearer token from the HttpServletRequest.
   *
   * @param request The HttpServletRequest object.
   * @return The value of the Authorization Bearer token.
   * @throws MissingServletRequestPartException If the Authorization Bearer token is missing.
   */
  public static String extractBearerToken(HttpServletRequest request)
      throws MissingServletRequestPartException {
    log.debug("Extracting bearer token from request");
    return extractHeader(request, AUTHORIZATION_HEADER, AUTHORIZATION_BEARER_PREFIX);
  }

  /**
   * Get the value of the Authorization Bearer token from the HttpServletRequest or return null if
   * the token is missing.
   *
   * @param request The HttpServletRequest object.
   * @return The value of the Authorization Bearer token or null if the token is missing.
   */
  public static String extractBearerTokenOrNull(HttpServletRequest request) {
    log.debug("Extracting bearer token from request");
    return extractHeaderOrNull(request, AUTHORIZATION_HEADER, AUTHORIZATION_BEARER_PREFIX);
  }

  /**
   * Get the value of a specific header from the HttpServletRequest.
   *
   * @param request The HttpServletRequest object.
   * @param headerName The name of the header to retrieve.
   * @return The value of the specified header.
   * @throws MissingServletRequestPartException If the header is missing.
   */
  public static String extractHeader(HttpServletRequest request, String headerName)
      throws MissingServletRequestPartException {
    String headerValue = request.getHeader(headerName);
    validateHeaderPresence(headerValue, headerName);
    return headerValue;
  }

  /**
   * Get the value of a header starting after a specific prefix.
   *
   * @param request The HttpServletRequest object.
   * @param headerName The name of the header to retrieve.
   * @param prefix The prefix of the header value.
   * @return The value of the header with the specified prefix.
   */
  public static String extractHeader(HttpServletRequest request, String headerName, String prefix)
      throws MissingServletRequestPartException {
    String headerValue = extractHeader(request, headerName);
    validateHeaderPrefix(headerValue, headerName, prefix);
    return headerValue.substring(prefix.length());
  }

  /**
   * Get the value of a header starting after a specific prefix or return null if the header is
   * missing.
   *
   * @param request The HttpServletRequest object.
   * @param headerName The name of the header to retrieve.
   * @param prefix The prefix of the header value.
   * @return The value of the header with the specified prefix or null if the header is missing.
   */
  public static String extractHeaderOrNull(
      HttpServletRequest request, String headerName, String prefix) {
    String headerValue = request.getHeader(headerName);
    if (!StringUtils.hasText(headerValue)) return null;
    return headerValue.startsWith(prefix) ? headerValue.substring(prefix.length()) : null;
  }

  /**
   * Validate the presence of a header in the HttpServletRequest.
   *
   * @param headerValue The value of the header.
   * @param headerName The name of the header.
   * @throws MissingServletRequestPartException If the header is missing.
   */
  private static void validateHeaderPresence(String headerValue, String headerName)
      throws MissingServletRequestPartException {
    if (!StringUtils.hasText(headerValue)) {
      log.warn("Header {} is missing", headerName);
      throw new MissingServletRequestPartException("Header " + headerName);
    }
  }

  /**
   * Validate the prefix of a header in the HttpServletRequest.
   *
   * @param headerValue The value of the header.
   * @param headerName The name of the header.
   * @param prefix The prefix of the header value.
   * @throws MissingServletRequestPartException If the header does not start with the specified
   *     prefix.
   */
  private static void validateHeaderPrefix(String headerValue, String headerName, String prefix)
      throws MissingServletRequestPartException {
    if (!headerValue.startsWith(prefix)) {
      log.warn("Header {} does not start with {}", headerName, prefix);
      throw new MissingServletRequestPartException(
          "Header " + headerName + " with prefix " + prefix);
    }
  }
}
