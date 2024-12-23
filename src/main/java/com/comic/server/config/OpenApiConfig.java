package com.comic.server.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    // servers = {
    //   @Server(url = "https://comic-production.up.railway.app"),
    // },
    // servers = {
    //   @Server(url = "https://80c3-14-169-94-210.ngrok-free.app"),
    // },
    info =
        @Info(
            title = "Comic API",
            version = "1.0",
            description =
                "Documentation Comic Service API v1.0, Base URL:"
                    + " https://comic-production.up.railway.app",
            termsOfService = "http://swagger.io/terms/",
            contact = @Contact(name = "Comic", email = "comic @gmail.com")))
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer")
public class OpenApiConfig {
  public static final String BEARER_AUTH_NAME = "Bearer Authentication";

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .components(new Components())
        .path(
            "/api/v1/logout",
            new PathItem()
                .post(
                    new Operation()
                        .operationId("logout")
                        .responses(
                            new ApiResponses()
                                .addApiResponse("200", new ApiResponse().description("OK")))
                        .security(List.of(new SecurityRequirement().addList(BEARER_AUTH_NAME)))
                        .tags(List.of("auth-controller"))
                        .summary("Logout the current user")
                        .description("Logout the current user.")));
  }
}
