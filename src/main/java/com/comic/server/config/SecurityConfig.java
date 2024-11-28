package com.comic.server.config;

import com.comic.server.feature.auth.jwt.AuthEntryPointJwt;
import com.comic.server.feature.auth.jwt.LazyJwtAuthTokenFilter;
import com.comic.server.utils.ApiEndpointSecurityInspector;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@Slf4j
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

  private final UserDetailsService userDetailsService;

  private final LazyJwtAuthTokenFilter lazyJwtAuthTokenFilter;
  private final AuthEntryPointJwt unauthorizedHandler;
  private final ApiEndpointSecurityInspector apiEndpointSecurityInspector;

  private final LogoutSuccessHandler logoutSuccessHandler;
  private final LogoutHandler logoutHandler;

  // @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
  // private String ISSUER_URL;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // @Bean
  private DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  // @Bean
  // public AuthenticationManager authenticationManager(List<AuthenticationProvider> providers)
  //     throws Exception {
  //   return new ProviderManager(providers);
  // }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
      throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public CorsConfigurationSource corsApiConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(
        List.of(
            "http://localhost:*", "*.ngrok-free.app", "https://comic-production.up.railway.app"));
    // configuration.addAllowedOriginPattern("*");
    // configuration.addAllowedHeader("*");
    // configuration.addAllowedMethod("*");
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);
    configuration.setAllowedMethods(
        List.of(
            "HEAD", "GET", "POST", "PUT", "DELETE", "PATCH",
            "OPTIONS")); // <-- methods allowed in CORS policy
    configuration.setAllowedHeaders(
        List.of(
            "Authorization",
            "Cache-Control",
            "Content-Type",
            "Accept",
            "X-Api-Key",
            "X-Forwarded-For",
            "X-Requested-With",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Headers",
            "Origin")); // <-- headers allowed in CORS policy
    configuration.setExposedHeaders(
        List.of(
            "Authorization",
            "Cache-Control",
            "Content-Type",
            "Accept",
            "X-Requested-With",
            "X-Rate-Limit-Retry-After-Seconds",
            "X-Rate-Limit-Remaining",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Headers",
            "Origin")); // <-- headers exposed in CORS policy
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

    apiEndpointSecurityInspector.addPublicEndpoint(
        "/api/v1/auth/**", "/api/actuator/**", "/login/oauth2/**");

    http.cors(cors -> cors.configurationSource(corsApiConfigurationSource()))
        .csrf(
            customizer -> {
              customizer.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
              customizer.ignoringRequestMatchers("/**", "/actuator/**");
            })

        // exception handling
        .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))

        // session management
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // authorize
        .authorizeHttpRequests(
            auth -> {
              Arrays.stream(HttpMethod.values())
                  .forEach(
                      method -> {
                        auth.requestMatchers(
                                method, apiEndpointSecurityInspector.getPublicSecurityPaths(method))
                            .permitAll();
                      });
              auth.anyRequest().authenticated();
            })
        .formLogin(formLogin -> formLogin.disable())

        // oauth2 login
        // .oauth2Login(
        //     oauth2 -> {
        //       oauth2
        //           // .clientRegistrationRepository(clientRegistrationRepository())
        //           .successHandler(new OAuth2LoginSuccessHandlerImpl())
        //           .defaultSuccessUrl("/api/v1/auth/oauth2/success")
        //           // .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
        //           .authorizationEndpoint(
        //               authorization -> authorization.baseUri("/api/v1/auth/oauth2/login"));
        //     })
        // .oauth2ResourceServer(server -> server.jwt(Customizer.withDefaults()))
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(lazyJwtAuthTokenFilter, UsernamePasswordAuthenticationFilter.class)
        .logout(
            logout ->
                logout
                    .logoutUrl("/api/v1/auth/logout")
                    .addLogoutHandler(logoutHandler)
                    .logoutSuccessHandler(logoutSuccessHandler));

    return http.build();
  }
}
