package com.comic.server.exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

@Getter
@Slf4j
public class ResourceNotFoundException extends BaseException {

  private final String resourceName;
  private final String fieldName;
  private final Object fieldValue;

  public <T> ResourceNotFoundException(
      Class<T> resourceName, String conflictFieldName, Object conflictFieldValue) {
    this(
        String.format(
            "%s already in use with %s : '%s'",
            resourceName, conflictFieldName, conflictFieldValue),
        resourceName,
        conflictFieldName,
        conflictFieldValue);
  }

  public <T> ResourceNotFoundException(
      String message,
      @NonNull Class<T> resourceName,
      @NonNull String conflictFieldName,
      Object conflictFieldValue) {
    super(HttpStatus.CONFLICT, message);
    try {
      resourceName.getDeclaredField(conflictFieldName);

      this.resourceName = resourceName.getSimpleName();
      this.fieldName = conflictFieldName;
      this.fieldValue = conflictFieldValue;
    } catch (NoSuchFieldException e) {
      log.error("Field {} not found in class {}", conflictFieldName, resourceName.getSimpleName());
      throw new RuntimeException(
          String.format(
              "Field %s not found in class %s", conflictFieldName, resourceName.getSimpleName()));
    }
  }
}
