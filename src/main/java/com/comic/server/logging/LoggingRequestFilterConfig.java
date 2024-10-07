// package com.foodey.server.logging;

// import jakarta.servlet.Filter;
// import lombok.RequiredArgsConstructor;
// import org.springframework.boot.web.servlet.FilterRegistrationBean;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// @RequiredArgsConstructor
// public class LoggingRequestFilterConfig {

//   private final LoggingRequestFilter loggingRequestFilter;

//   @Bean
//   public FilterRegistrationBean<Filter> loggingFilter() {
//     FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();

//     registrationBean.setFilter(loggingRequestFilter);
//     registrationBean.setAsyncSupported(true);
//     registrationBean.addUrlPatterns("/*");

//     return registrationBean;
//   }
// }
