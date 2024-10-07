package com.comic.server.config;

import com.comic.server.feature.user.model.User;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

  // https://stackoverflow.com/questions/29472931/how-does-createdby-work-in-spring-data-jpa
  @Bean
  @Primary
  public AuditorAware<String> auditorProvider() {
    return () -> {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication == null || !authentication.isAuthenticated()) {
        return Optional.empty();
      }
      Object principal = authentication.getPrincipal();
      if (!(principal instanceof User)) {
        return Optional.empty();
      }
      return Optional.of(((User) principal).getId());
    };
  }
}
