// package com.foodey.server.logging;

// import jakarta.servlet.http.HttpServletRequest;
// import java.lang.reflect.Type;
// import lombok.RequiredArgsConstructor;
// import org.springframework.core.MethodParameter;
// import org.springframework.http.HttpInputMessage;
// import org.springframework.http.converter.HttpMessageConverter;
// import org.springframework.web.bind.annotation.ControllerAdvice;
// import org.springframework.web.context.request.RequestAttributes;
// import org.springframework.web.context.request.RequestContextHolder;
// import org.springframework.web.context.request.ServletRequestAttributes;
// import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

// @ControllerAdvice
// @RequiredArgsConstructor
// public class CustomRequestBodyAdviceAdapter extends RequestBodyAdviceAdapter {

//   private final LoggingHttpRequestService loggingService;

//   @Override
//   public boolean supports(
//       MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>>
// aClass) {
//     return true;
//   }

//   @Override
//   public Object afterBodyRead(
//       Object body,
//       HttpInputMessage inputMessage,
//       MethodParameter parameter,
//       Type targetType,
//       Class<? extends HttpMessageConverter<?>> converterType) {
//     RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
//     if (attributes != null) {
//       HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
//       loggingService.logRequest(request, body);
//     }
//     return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
//   }
// }
