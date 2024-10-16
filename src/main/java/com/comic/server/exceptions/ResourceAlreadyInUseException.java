package com.comic.server.exceptions;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

@Getter
@Slf4j
public class ResourceAlreadyInUseException extends BaseException {

  private String resourceName;
  private Map<String, Object> conflictFields = new HashMap<>();

  // private String fieldName;
  // private Object fieldValue;

  public <T> ResourceAlreadyInUseException(
      Class<T> resourceName, String conflictFieldName, Object conflictFieldValue) {
    this(
        String.format(
            "%s already in use with %s : '%s'",
            resourceName, conflictFieldName, conflictFieldValue),
        resourceName,
        conflictFieldName,
        conflictFieldValue);
  }

  public <T> ResourceAlreadyInUseException(
      String message,
      @NonNull Class<T> resourceName,
      @NonNull String conflictFieldName,
      Object conflictFieldValue) {
    super(HttpStatus.CONFLICT, message);
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

  public <T> ResourceAlreadyInUseException(
      Class<T> resourceName, Map<String, Object> conflictFields) {
    this(
        String.format("%s already in use with %s", resourceName, conflictFields.toString()),
        resourceName,
        conflictFields);
  }

  public <T> ResourceAlreadyInUseException(
      String message, @NonNull Class<T> resourceName, Map<String, Object> conflictFields) {
    super(HttpStatus.CONFLICT, message);
    try {

      for (String conflictField : conflictFields.keySet()) {
        resourceName.getDeclaredField(conflictField);
      }

      this.resourceName = resourceName.getSimpleName();
      this.conflictFields = conflictFields;

    } catch (NoSuchFieldException e) {
      log.error(
          "Field {} not found in class {}",
          conflictFields.toString(),
          resourceName.getSimpleName());
      throw new RuntimeException(
          String.format(
              "Field %s not found in class %s",
              conflictFields.toString(), resourceName.getSimpleName()));
    }
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
