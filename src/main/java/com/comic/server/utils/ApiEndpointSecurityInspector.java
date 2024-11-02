package com.comic.server.utils;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import com.comic.server.annotation.PublicEndpoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Utility class responsible for evaluating the accessibility of API endpoints based on their
 * security configuration. It works in conjunction with the mappings of controller methods annotated
 * with {@link PublicEndpoint}.
 */
@Component
@Slf4j
public class ApiEndpointSecurityInspector {

  @Getter
  public static class PathWithCondition {
    private String path;
    private boolean filterJwt;

    public PathWithCondition(String path, boolean filterJwt) {
      this.path = path;
      this.filterJwt = filterJwt;
    }

    public PathWithCondition(String path) {
      this(path, false);
    }

    @Override
    public String toString() {
      return path;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) return false;
      else if (obj == this) return true;
      else if (!(obj instanceof PathWithCondition)) return false;
      PathWithCondition other = (PathWithCondition) obj;
      return this.path.equals(other.path);
    }

    @Override
    public int hashCode() {
      return path.hashCode();
    }
  }

  @Value("${spring.profiles.active}")
  private String PROFILE;

  private RequestMappingHandlerMapping requestHandlerMapping;
  private final AntPathMatcher antPathMatcher = new AntPathMatcher();

  // /** A set of public endpoints that are accessible via any HTTP method. */
  // private Set<PathWithCondition> publicAllMethodsEndpoints = new HashSet<>();

  public ApiEndpointSecurityInspector(
      @Qualifier("requestMappingHandlerMapping")
          RequestMappingHandlerMapping requestHandlerMapping) {

    this.requestHandlerMapping = requestHandlerMapping;
  }

  private final Map<HttpMethod, Set<PathWithCondition>> publicEndpoints =
      new HashMap<>() {
        {
          put(
              GET,
              new HashSet<PathWithCondition>() {
                {
                  add(new PathWithCondition("/v3/api-docs**/**"));
                  add(new PathWithCondition("/swagger-ui**/**"));
                  add(new PathWithCondition("/.well-known**/**"));
                }
              });
          put(POST, new HashSet<PathWithCondition>());
        }
      };

  /**
   * Adds a public endpoint that is accessible via any HTTP method.
   *
   * @param path The path of the public endpoint.
   */
  public void addPublicEndpoint(String path) {
    addPublicEndpoint(false, path);
  }

  /**
   * Adds a public endpoint that is accessible via any HTTP method.
   *
   * @param paths The paths of the public endpoints.
   */
  public void addPublicEndpoint(String... paths) {
    Arrays.stream(paths).forEach(path -> addPublicEndpoint(path));
  }

  /**
   * Adds a public endpoint that is accessible via any HTTP method.
   *
   * @param path The path of the public endpoint.
   * @param filterJwt Whether to filter JWT for the endpoint.
   */
  public void addPublicEndpoint(boolean filterJwt, String path) {
    Arrays.stream(HttpMethod.values())
        .forEach(
            httpMethod -> {
              addPublicEndpoint(httpMethod, filterJwt, path);
            });
  }

  /**
   * Adds a public endpoint that is accessible via any HTTP method.
   *
   * @param paths The paths of the public endpoints.
   */
  public void addPublicEndpoint(boolean filterJwt, String... paths) {
    Arrays.stream(paths).forEach(path -> addPublicEndpoint(filterJwt, path));
  }

  /**
   * Adds a public endpoint that is accessible via any HTTP method.
   *
   * @param path The path of the public endpoint.
   */
  public void addPublicEndpoint(HttpMethod httpMethod, String path) {
    addPublicEndpoint(httpMethod, false, path);
  }

  /**
   * Adds a public endpoint that is accessible via any HTTP method.
   *
   * @param path The path of the public endpoint.
   * @param filterJwt Whether to filter JWT for the endpoint.
   */
  public void addPublicEndpoint(HttpMethod httpMethod, String... paths) {
    Arrays.stream(paths).forEach(path -> addPublicEndpoint(httpMethod, path));
  }

  /**
   * Adds a public endpoint that is accessible via any HTTP method.
   *
   * @param paths The paths of the public endpoints.
   */
  public void addPublicEndpoint(HttpMethod httpMethod, boolean filterJwt, String... paths) {
    Arrays.stream(paths).forEach(path -> addPublicEndpoint(httpMethod, filterJwt, path));
  }

  /**
   * Adds a public endpoint that is accessible via any HTTP method.
   *
   * @param path The path of the public endpoint.
   * @param filterJwt Whether to filter JWT for the endpoint.
   */
  public void addPublicEndpoint(HttpMethod httpMethod, boolean filterJwt, String path) {

    if (publicEndpoints.get(httpMethod) == null) {
      publicEndpoints.put(httpMethod, new HashSet<PathWithCondition>());
    }
    publicEndpoints.get(httpMethod).add(new PathWithCondition(path, filterJwt));
    log.info("Added public endpoint: " + path + " for " + httpMethod);
  }

  /**
   * Initializes the class by gathering public endpoints for various HTTP methods. It identifies
   * designated public endpoints within the application's mappings and adds them to separate lists
   * based on their associated HTTP methods. If OpenAPI is enabled, Swagger endpoints are also
   * considered as public. The method is annotated with {@link PostConstruct} to ensure that the
   */
  @PostConstruct
  public void init() {
    final var handlerMethods = requestHandlerMapping.getHandlerMethods();

    handlerMethods.forEach(
        (requestInfo, handlerMethod) -> {
          // check if the method is annotated with PublicEndpoint or the parent class is annotated
          //

          var annotation = handlerMethod.getMethodAnnotation(PublicEndpoint.class);

          if (annotation == null) {
            annotation = handlerMethod.getBeanType().getAnnotation(PublicEndpoint.class);
          }

          if (annotation != null) {
            // if (handlerMethod.hasMethodAnnotation(PublicEndpoint.class)
            //     || handlerMethod.getBeanType().isAnnotationPresent(PublicEndpoint.class)) {

            List<String> profilesList = Arrays.asList(annotation.profiles());
            boolean filterJwt = annotation.filterJwt();

            if (profilesList.isEmpty() || profilesList.contains(PROFILE)) {
              final Set<String> apiPaths = requestInfo.getPatternValues();
              final Set<PathWithCondition> apiPathsWithCondition =
                  apiPaths.stream()
                      .map(path -> new PathWithCondition(path, filterJwt))
                      .collect(Collectors.toSet());

              requestInfo.getMethodsCondition().getMethods().stream()
                  .forEach(
                      httpMethod ->
                          publicEndpoints
                              .get(httpMethod.asHttpMethod())
                              .addAll(apiPathsWithCondition));
            }
          }
        });

    try {
      log.info(
          "Initializes public endpoints: "
              + new ObjectMapper()
                  .writerWithDefaultPrettyPrinter()
                  .writeValueAsString(publicEndpoints));
    } catch (Exception e) {
      log.info("Initializes public endpoints: " + publicEndpoints);
    }
  }

  /**
   * Checks if the provided HTTP request is directed towards an optional API endpoint that does not
   * require a JWT token but still filters it.
   *
   * @param request The HTTP request to inspect.
   * @return {@code true} if the request is to an optional API endpoint that does not require a JWT
   *     token, {@code false} otherwise.
   */
  public boolean isOptionalJwtSecurityPath(@NonNull final HttpServletRequest request) {
    return publicEndpoints
        .getOrDefault(HttpMethod.valueOf(request.getMethod()), Collections.emptySet())
        .stream()
        .anyMatch(
            apiPathWithCondition ->
                antPathMatcher.match(apiPathWithCondition.path, request.getRequestURI()));
  }

  /**
   * Checks if the provided HTTP request is directed towards an unsecured API endpoint.
   *
   * @param request The HTTP request to inspect.
   * @return {@code true} if the request is to an unsecured API endpoint, {@code false} otherwise.
   */
  public boolean isUnsecureRequest(@NonNull final HttpServletRequest request) {
    return getUnsecuredApiPaths(HttpMethod.valueOf(request.getMethod())).stream()
        .anyMatch(apiPath -> antPathMatcher.match(apiPath, request.getRequestURI()));
  }

  /**
   * Checks if the provided HTTP request is directed towards an unsecured API endpoint that does not
   * require a JWT token.
   *
   * @param request The HTTP request to inspect.
   * @return {@code true} if the request is to an unsecured API endpoint that does not require a JWT
   *     token, {@code false} otherwise.
   */
  public boolean isUnsecureJwtRequest(@NonNull final HttpServletRequest request) {
    return getUnsecuredJwtApiPaths(HttpMethod.valueOf(request.getMethod())).stream()
        .anyMatch(apiPath -> antPathMatcher.match(apiPath, request.getRequestURI()));
  }

  /**
   * Retrieves the list of unsecured API paths based on the provided HTTP method that do not require
   * a JWT token.
   *
   * @param httpMethod The HTTP method for which unsecured paths are to be retrieved.
   * @return A list of unsecured API paths for the specified HTTP method that do not require a JWT
   *     token.
   */
  private Set<String> getUnsecuredJwtApiPaths(@NonNull final HttpMethod httpMethod) {
    return publicEndpoints.getOrDefault(httpMethod, Collections.emptySet()).stream()
        .filter(apiPathsWithCondition -> !apiPathsWithCondition.filterJwt)
        .map(PathWithCondition::toString)
        .collect(Collectors.toSet());
  }

  /**
   * Retrieves the list of unsecured API paths based on the provided HTTP method.
   *
   * @param httpMethod The HTTP method for which unsecured paths are to be retrieved.
   * @return A list of unsecured API paths for the specified HTTP method.s
   */
  private Set<String> getUnsecuredApiPaths(@NonNull final HttpMethod httpMethod) {
    return publicEndpoints.getOrDefault(httpMethod, Collections.emptySet()).stream()
        .map(PathWithCondition::toString)
        .collect(Collectors.toSet());
  }

  /**
   * Retrieves the list of public API paths based on the provided HTTP method.
   *
   * @param httpMethod The HTTP method for which public paths are to be retrieved.
   * @return A list of public API paths for the specified HTTP method.
   */
  public String[] getPublicSecurityPaths(HttpMethod httpMethod) {
    return publicEndpoints.getOrDefault(httpMethod, Collections.emptySet()).stream()
        .map(PathWithCondition::toString)
        .toArray(String[]::new);
  }
}
