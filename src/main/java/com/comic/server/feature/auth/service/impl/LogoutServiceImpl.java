package com.comic.server.feature.auth.service.impl;

import com.comic.server.exceptions.JwtTokenException;
import com.comic.server.exceptions.ResourceNotFoundException;
import com.comic.server.feature.auth.enums.TokenType;
import com.comic.server.feature.auth.model.RefreshToken;
import com.comic.server.feature.auth.repository.RefreshTokenRepository;
import com.comic.server.utils.HttpHeaderUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Service
public record LogoutServiceImpl(
    @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
    RefreshTokenRepository refreshTokenRepository)
    implements LogoutHandler {

  @Override
  @Transactional
  public void logout(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    try {
      final String refreshToken = HttpHeaderUtils.extractBearerToken(request);

      RefreshToken savedRefreshToken =
          refreshTokenRepository
              .findByToken(refreshToken)
              .orElseThrow(
                  () ->
                      new JwtTokenException(
                          TokenType.BEARER, refreshToken, "Invalid refresh token"));

      refreshTokenRepository.save(savedRefreshToken.revoke());

      // NOTE: Need to think more because user can logout from multiple devices
      // User user = PrincipalUtils.getUser();
      // user.setLastLogoutAt(savedRefreshToken.getRevokedAt());
      // userService.save(user);

      response.setStatus(HttpStatus.NO_CONTENT.value());

    } catch (ResourceNotFoundException notFoundException) {
      resolver.resolveException(request, response, null, notFoundException);
    } catch (MissingServletRequestPartException httpHeaderMissingException) {
      resolver.resolveException(request, response, null, httpHeaderMissingException);
    } catch (Exception e) {
      resolver.resolveException(request, response, null, e);
    }
  }
}
