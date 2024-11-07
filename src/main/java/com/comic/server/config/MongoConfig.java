package com.comic.server.config;

import com.comic.server.feature.user.model.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableMongoAuditing
@RequiredArgsConstructor
public class MongoConfig {

  // https://stackoverflow.com/questions/29472931/how-does-createdby-work-in-spring-data-jpa
  @Bean
  public AuditorAware<String> auditorProvider() {
    return () -> {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication == null || !authentication.isAuthenticated()) {
        throw new AuthenticationException("User is not authenticated") {};
      }
      Object principal = authentication.getPrincipal();
      if (!(principal instanceof User)) {
        throw new AuthenticationException("User is not authenticated") {};
      }
      return Optional.of(((User) principal).getId());
    };
  }

  @Bean
  @Primary
  public AuditorAware<ObjectId> auditorObjectIdProvider() {
    return () -> {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication == null || !authentication.isAuthenticated()) {
        throw new AuthenticationException("User is not authenticated") {};
      }
      Object principal = authentication.getPrincipal();
      if (!(principal instanceof User)) {
        throw new AuthenticationException("User is not authenticated") {};
      }
      return Optional.of(new ObjectId(((User) principal).getId()));
    };
  }
}
