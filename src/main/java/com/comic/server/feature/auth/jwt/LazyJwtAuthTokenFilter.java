package com.comic.server.feature.auth.jwt;

import com.comic.server.exceptions.JwtTokenException;
import com.comic.server.feature.user.model.User;
import com.comic.server.feature.user.repository.UserRepository;
import com.comic.server.utils.ApiEndpointSecurityInspector;
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
    boolean skip = apiEndpointSecurityInspector.isUnsecureJwtRequest(request);
    log.info(request.getRequestURI() + " is unsecure jwt: " + skip);
    return skip;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    log.info("Processing jwt authentication for '{}'", request.getRequestURI());

    boolean isOptional = apiEndpointSecurityInspector.isOptionalJwtSecurityPath(request);

    try {
      SecurityContext context = SecurityContextHolder.getContext();

      Authentication authentication = context.getAuthentication();

      if (authentication == null || (isAnonymousUser(authentication) && isOptional)) {
        String jwtToken = HttpHeaderUtils.extractBearerToken(request).orElse(null);
        if (jwtToken == null) {
          if (isOptional) filterChain.doFilter(request, response);
          else resolveException(request, response, new JwtTokenException("Missing JWT token"));
          return;
        }

        String userPubId = jwtService.extractSubject(jwtToken);

        if (userPubId != null) {
          log.debug("Processing authentication for userPubId: {}", userPubId);

          User user = userRepository.findByPubId(userPubId).orElse(null);
          if (user == null) {
            resolveException(request, response, new JwtTokenException(jwtToken, "User not found"));
            return;
          } else if (jwtService.isAccessTokenValid(jwtToken, user)) {
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

    } catch (Exception e) {
      resolver.resolveException(request, response, null, e);
    }
  }

  private boolean isAnonymousUser(Authentication authentication) {
    return authentication.getName() == "anonymousUser";
  }

  private void resolveException(
      HttpServletRequest request, HttpServletResponse response, Exception ex) {
    resolver.resolveException(request, response, null, ex);
  }
}
