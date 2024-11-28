package com.comic.server.utils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
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
   * @return Optional containing the Authorization Bearer token, or empty if not present.
   */
  public static Optional<String> extractBearerToken(HttpServletRequest request) {
    log.debug("Extracting bearer token from request");
    return extractHeaderWithPrefix(request, AUTHORIZATION_HEADER, AUTHORIZATION_BEARER_PREFIX);
  }

  /**
   * Get the value of the Authorization Bearer token and throw an exception if not present.
   *
   * @param request The HttpServletRequest object.
   * @return The Authorization Bearer token.
   * @throws MissingServletRequestPartException if the token is missing or invalid.
   */
  public static String extractBearerTokenOrThrow(HttpServletRequest request)
      throws MissingServletRequestPartException {
    return extractBearerToken(request)
        .orElseThrow(() -> new MissingServletRequestPartException(AUTHORIZATION_HEADER));
  }

  /**
   * Get the value of a specific header from the HttpServletRequest.
   *
   * @param request The HttpServletRequest object.
   * @param headerName The name of the header to retrieve.
   * @return Optional containing the header value, or empty if not present.
   */
  public static Optional<String> extractHeader(HttpServletRequest request, String headerName) {
    String headerValue = request.getHeader(headerName);
    if (StringUtils.hasText(headerValue)) {
      return Optional.of(headerValue);
    }
    log.warn("Header {} is missing", headerName);
    return Optional.empty();
  }

  /**
   * Get the value of a specific header and throw an exception if not present.
   *
   * @param request The HttpServletRequest object.
   * @param headerName The name of the header to retrieve.
   * @return The header value.
   * @throws MissingServletRequestPartException if the header is missing.
   */
  public static String extractHeaderOrThrow(HttpServletRequest request, String headerName)
      throws MissingServletRequestPartException {
    return extractHeader(request, headerName)
        .orElseThrow(() -> new MissingServletRequestPartException("Header " + headerName));
  }

  /**
   * Get the value of a header starting after a specific prefix.
   *
   * @param request The HttpServletRequest object.
   * @param headerName The name of the header to retrieve.
   * @param prefix The prefix of the header value.
   * @return Optional containing the header value with the prefix removed, or empty if not valid.
   */
  public static Optional<String> extractHeaderWithPrefix(
      HttpServletRequest request, String headerName, String prefix) {
    return extractHeader(request, headerName)
        .filter(
            headerValue -> {
              if (headerValue.startsWith(prefix)) return true;
              log.warn("Header {} does not start with {}", headerName, prefix);
              return false;
            })
        .map(headerValue -> headerValue.substring(prefix.length()));
  }

  /**
   * Get the value of a header starting after a specific prefix and throw an exception if invalid.
   *
   * @param request The HttpServletRequest object.
   * @param headerName The name of the header to retrieve.
   * @param prefix The prefix of the header value.
   * @return The header value with the prefix removed.
   * @throws MissingServletRequestPartException if the header is missing or invalid.
   */
  public static String extractHeaderWithPrefixOrThrow(
      HttpServletRequest request, String headerName, String prefix)
      throws MissingServletRequestPartException {
    return extractHeaderWithPrefix(request, headerName, prefix)
        .orElseThrow(
            () ->
                new MissingServletRequestPartException(
                    "Header " + headerName + " with prefix " + prefix + " is missing or invalid"));
  }
}
