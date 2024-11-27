package com.comic.server.feature.auth.jwt;

import com.comic.server.exceptions.JwtTokenException;
import com.comic.server.feature.user.model.User;
import com.comic.server.feature.user.repository.UserRepository;
import com.comic.server.utils.ApiEndpointSecurityInspector;
import com.comic.server.utils.ConsoleUtils;
import com.comic.server.utils.HttpHeaderUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Slf4j
@Component
public class LazyJwtAuthTokenFilter extends OncePerRequestFilter {

  private JwtService jwtService;
  private UserRepository userRepository;
  private ApiEndpointSecurityInspector apiEndpointSecurityInspector;
  private HandlerExceptionResolver resolver;

  public LazyJwtAuthTokenFilter(
      JwtService jwtService,
      UserRepository userRepository,
      ApiEndpointSecurityInspector apiEndpointSecurityInspector,
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
    this.jwtService = jwtService;
    this.userRepository = userRepository;
    this.apiEndpointSecurityInspector = apiEndpointSecurityInspector;
    this.resolver = resolver;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    return apiEndpointSecurityInspector.isUnsecureJwtRequest(request);
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    boolean isOptionalJwtSecurityPath =
        apiEndpointSecurityInspector.isOptionalJwtSecurityPath(request);

    try {
      SecurityContext context = SecurityContextHolder.getContext();

      Authentication authentication = context.getAuthentication();

      if (authentication == null
          || (authentication.getName() == "anonymousUser" && isOptionalJwtSecurityPath)) {
        ConsoleUtils.prettyPrint("test");

        String jwtToken = HttpHeaderUtils.extractBearerToken(request);

        String userPubId = jwtService.extractSubject(jwtToken);

        if (userPubId != null) {
          log.debug("Processing authentication for userPubId: {}", userPubId);

          User user =
              userRepository
                  .findByPubId(userPubId)
                  .orElseThrow(() -> new JwtTokenException(jwtToken, "User not found"));

          if (jwtService.isAccessTokenValid(jwtToken, user)) {
            UsernamePasswordAuthenticationToken usernameAuthentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            usernameAuthentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));
            context.setAuthentication(usernameAuthentication);

            log.debug("User {} successfully authenticated with pubId {}", user.getId(), userPubId);
          }
        }
      }
      filterChain.doFilter(request, response);

    } catch (MissingServletRequestPartException e) {
      if (isOptionalJwtSecurityPath) {
        filterChain.doFilter(request, response);
      } else {
        resolver.resolveException(request, response, null, e);
      }
    } catch (Exception e) {
      resolver.resolveException(request, response, null, e);
    }
  }
}
