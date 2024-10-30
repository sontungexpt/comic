package com.comic.server.exceptions;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

@Getter
@Slf4j
public class ResourceNotFoundException extends BaseException {

  private String resourceName;

  private Map<String, Object> conflictFields = new HashMap<>();

  public <T> ResourceNotFoundException(
      Class<T> resourceName, String conflictFieldName, Object conflictFieldValue) {
    this(
        String.format(
            "%s not found with %s : '%s'", resourceName, conflictFieldName, conflictFieldValue),
        resourceName,
        conflictFieldName,
        conflictFieldValue);
  }

  public <T> ResourceNotFoundException(
      String message,
      @NonNull Class<T> resourceName,
      @NonNull String conflictFieldName,
      Object conflictFieldValue) {
    super(HttpStatus.NOT_FOUND, message);
    try {
      resourceName.getDeclaredField(conflictFieldName);

      this.resourceName = resourceName.getSimpleName();
      this.conflictFields.put(conflictFieldName, conflictFieldValue);
    } catch (NoSuchFieldException e) {
      log.error("Field {} not found in class {}", conflictFieldName, resourceName.getSimpleName());
      throw new RuntimeException(
          String.format(
              "Field %s not found in class %s", conflictFieldName, resourceName.getSimpleName()));
    }
  }

  public <T> ResourceNotFoundException(Class<T> resourceName, Map<String, Object> conflictFields) {
    this(
        String.format("%s not found with %s", resourceName, conflictFields.toString()),
        resourceName,
        conflictFields);
  }

  public <T> ResourceNotFoundException(
      String message, @NonNull Class<T> resourceName, Map<String, Object> conflictFields) {
    super(HttpStatus.NOT_FOUND, message);
    this.resourceName = resourceName.getSimpleName();
    this.conflictFields = conflictFields;
  }

  @Override
  public Object getData() {
    return new Object() {
      public Map<String, Object> getConflictFields() {
        return conflictFields;
      }
    };
  }
}
