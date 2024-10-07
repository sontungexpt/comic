package com.comic.server.common.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuperBuilder
public class ApiResponse {

  private int code;
  private Object data;
  private String timestamp;
  private String cause;
  private String path;

  private HttpStatus status;

  public ResponseEntity<ApiResponse> toResponseEntity() {
    return new ResponseEntity<>(this, this.status);
  }

  public static ApiResponseBuilder<?, ?> builder(HttpStatus status) {
    return new ApiResponseBuilderImpl().status(status);
  }

  public abstract static class ApiResponseBuilder<
      C extends ApiResponse, B extends ApiResponseBuilder<C, B>> {

    public ApiResponseBuilder<C, B> request(HttpServletRequest request) {
      return self().path(request.getRequestURI());
    }
  }
}
