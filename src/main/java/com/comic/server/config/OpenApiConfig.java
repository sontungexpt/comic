package com.comic.server.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info =
        @Info(
            title = "Comic API",
            version = "1.0",
            description = "Documentation Comic Service API v1.0",
            termsOfService = "http://swagger.io/terms/",
            contact = @Contact(name = "Comic", email = "comic @gmail.com")))
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer")
public class OpenApiConfig {

  public static final String BEARER_AUTH_NAME = "Bearer Authentication";
}
