package com.comic.server.resolver;

import com.comic.server.annotation.BearerToken;
import com.comic.server.utils.HttpHeaderUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

public class BearerTokenArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(BearerToken.class);
  }

  @Override
  public Object resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      org.springframework.web.bind.support.WebDataBinderFactory binderFactory)
      throws Exception {
    HttpServletRequest request =
        (HttpServletRequest) webRequest.getNativeRequest(HttpServletRequest.class);
    BearerToken annotation = parameter.getParameterAnnotation(BearerToken.class);

    try {
      return HttpHeaderUtils.extractBearerToken(request);
    } catch (MissingServletRequestPartException e) {
      if (annotation.throwException()) {
        throw e;
      }
      return null;
    }
  }
}
