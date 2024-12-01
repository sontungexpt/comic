package com.comic.server.utils;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import com.comic.server.annotation.PublicEndpoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
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
  private class APIPath {
    private String path;
    private boolean filterJwt;

    public APIPath(String path, boolean filterJwt) {
      this.path = path;
      this.filterJwt = filterJwt;
    }

    public APIPath(String path) {
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
      else if (!(obj instanceof APIPath)) return false;
      APIPath other = (APIPath) obj;
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
  private final Map<String, Boolean> staticEndpoints = new HashMap<>();

  // /** A set of public endpoints that are accessible via any HTTP method. */
  // private Set<PathWithCondition> publicAllMethodsEndpoints = new HashSet<>();

  public ApiEndpointSecurityInspector(
      @Qualifier("requestMappingHandlerMapping")
          RequestMappingHandlerMapping requestHandlerMapping) {

    this.requestHandlerMapping = requestHandlerMapping;
  }

  private final Map<HttpMethod, Set<APIPath>> publicEndpoints =
      new HashMap<>() {
        {
          put(
              GET,
              new HashSet<APIPath>() {
                {
                  add(new APIPath("/v3/api-docs**/**"));
                  add(new APIPath("/swagger-ui**/**"));
                  add(new APIPath("/.well-known**/**"));
                  add(new APIPath("/test/**"));
                  add(new APIPath("/api/v1/test/**"));
                  add(new APIPath("/favicon.ico"));
                }
              });
          put(POST, new HashSet<APIPath>());
        }
      };

  private Set<APIPath> getPublicEndpoints(HttpMethod httpMethod) {
    if (publicEndpoints.get(httpMethod) == null) {
      publicEndpoints.put(httpMethod, new HashSet<APIPath>());
    }
    return publicEndpoints.get(httpMethod);
  }

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
    getPublicEndpoints(httpMethod).add(new APIPath(path, filterJwt));
    addStaticEndpoint(path, httpMethod);
    log.info("Added public endpoint: " + path + " for " + httpMethod);
  }

  private void watchStaticEndpoints(Map<RequestMappingInfo, HandlerMethod> handlerMethods) {

    publicEndpoints.forEach(
        (httpMethod, apiPaths) -> {
          apiPaths.forEach(apiPath -> addStaticEndpoint(apiPath.path, httpMethod));
        });

    handlerMethods.forEach(
        (requestInfo, handlerMethod) -> {
          requestInfo
              .getMethodsCondition()
              .getMethods()
              .forEach(
                  httpMethod -> {
                    requestInfo
                        .getPatternValues()
                        .forEach(path -> addStaticEndpoint(path, httpMethod.asHttpMethod()));
                  });
        });
  }

  public void watchEachPublicEndpoints(RequestMappingInfo requestInfo, boolean filterJwt) {
    requestInfo
        .getMethodsCondition()
        .getMethods()
        .forEach(
            httpMethod -> {
              getPublicEndpoints(httpMethod.asHttpMethod())
                  .addAll(
                      requestInfo.getPatternValues().stream()
                          .map(path -> new APIPath(path, filterJwt))
                          .collect(Collectors.toSet()));
            });
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
    watchStaticEndpoints(handlerMethods);

    handlerMethods.forEach(
        (requestInfo, handlerMethod) -> {

          // check if the method is annotated with PublicEndpoint or the parent class is
          // annotated
          var annotation = getAnnotation(handlerMethod, PublicEndpoint.class);

          if (annotation != null) {
            List<String> profilesList = getProfiles(annotation, handlerMethod);
            if (profilesList.isEmpty() || profilesList.contains(PROFILE)) {
              watchEachPublicEndpoints(requestInfo, annotation.filterJwt());
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
        .anyMatch(apiPath -> matchPath(apiPath.path, request));
  }

  /**
   * Checks if the provided HTTP request is directed towards an unsecured API endpoint.
   *
   * @param request The HTTP request to inspect.
   * @return {@code true} if the request is to an unsecured API endpoint, {@code false} otherwise.
   */
  public boolean isUnsecureRequest(@NonNull final HttpServletRequest request) {
    return getUnsecuredApiPaths(HttpMethod.valueOf(request.getMethod())).stream()
        .anyMatch(apiPath -> matchPath(apiPath, request));
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
        .anyMatch(apiPath -> matchPath(apiPath, request));
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
        .filter(apiPath -> !apiPath.filterJwt)
        .map(APIPath::toString)
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
        .map(APIPath::toString)
        .collect(Collectors.toSet());
  }

  /**
   * Retrieves the list of public API paths based on the provided HTTP method.
   *
   * @param httpMethod The HTTP metrhod for which public paths are to be retrieved.
   * @return A list of public API paths for the specified HTTP method.
   */
  public String[] getPublicSecurityPaths(HttpMethod httpMethod) {
    return publicEndpoints.getOrDefault(httpMethod, Collections.emptySet()).stream()
        .map(APIPath::toString)
        .toArray(String[]::new);
  }

  @Nullable
  private <A extends Annotation> A getAnnotation(
      HandlerMethod annotatedMethod, Class<A> annotationClass) {
    if (annotatedMethod == null) return null;
    A annotation = annotatedMethod.getMethodAnnotation(annotationClass);
    if (annotation != null) return annotation;
    annotation = annotatedMethod.getBeanType().getAnnotation(annotationClass);
    return annotation;
  }

  private boolean isPathVariablePattern(@NonNull String path) {
    boolean uriVar = false;
    for (int i = 0; i < path.length(); i++) {
      char c = path.charAt(i);
      if (c == '{') {
        uriVar = true;
      } else if (c == '}' && uriVar) {
        return true;
      }
    }
    return false;
  }

  private boolean matchPath(String path, String requestPath, String methodName) {
    if (path == null || requestPath == null) return false;
    boolean matched = antPathMatcher.match(path, requestPath);
    if (!isPathVariablePattern(path)) return matched;
    return matched && !staticEndpointExists(requestPath, methodName);
  }

  private boolean matchPath(String path, HttpServletRequest request) {
    return matchPath(path, request.getRequestURI(), request.getMethod());
  }

  private void addStaticEndpoint(String path, HttpMethod method) {
    addStaticEndpoint(path, method.name());
  }

  private void addStaticEndpoint(String path, String methodName) {
    if (antPathMatcher.isPattern(path)) return;
    staticEndpoints.put(prefixByMethodName(path, methodName), true);
  }

  private boolean staticEndpointExists(String path, String methodName) {
    return staticEndpoints.containsKey(prefixByMethodName(path, methodName));
  }

  private String prefixByMethodName(String path, String methodName) {
    return methodName + "_" + path;
  }

  private List<String> getProfiles(
      @NonNull PublicEndpoint annotation, HandlerMethod handlerMethod) {
    List<String> profilesList = new ArrayList<>();
    for (String profile : annotation.profiles()) {
      profilesList.add(profile);
    }
    Profile profileAnnotation = getAnnotation(handlerMethod, Profile.class);
    if (profileAnnotation != null) {
      for (String profile : profileAnnotation.value()) {
        profilesList.add(profile);
      }
    }
    return profilesList;
  }
}
