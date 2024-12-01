package com.comic.server.annotation;

import com.comic.server.feature.auth.jwt.LazyJwtAuthTokenFilter;
import com.comic.server.utils.ApiEndpointSecurityInspector;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark the endpoint as public.
 *
 * <p>{@link ApiEndpointSecurityInspector}
 *
 * <p>{@link Profile}
 *
 * <p>{@link SecurityConfig}
 *
 * <p>{@link LazyJwtAuthTokenFilter}
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PublicEndpoint {

  /**
   * The profiles that are allowed to access the endpoint. You can use hte @Profile annotation to
   * specify the profiles that are allowed to access the endpoint. istead of using the profiles
   * attribute of the @PublicEndpoint annotation.
   */
  String[] profiles() default {};

  /**
   * If true, the endpoint will process the request by checking the JWT token. If the user is
   * authenticated (i.e., a valid JWT token is provided), the request will proceed with the context
   * of the authenticated user (to identify who the user is). If no valid JWT token is provided, the
   * endpoint will still function as a public endpoint and process the request without any
   * user-specific context. If false, the endpoint will process the request without checking the JWT
   * token. This is useful for endpoints that do not require any user-specific context.
   *
   * <p>By default, this attribute is set to false .
   */
  boolean filterJwt() default false;
}
