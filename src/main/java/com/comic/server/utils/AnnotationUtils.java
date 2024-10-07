package com.comic.server.utils;

import java.lang.reflect.Field;
import org.springframework.data.annotation.Id;

public class AnnotationUtils {

  public static boolean hasIdAnnotationField(Class<?> clazz) {
    for (Field field : clazz.getDeclaredFields()) {
      if (field.isAnnotationPresent(Id.class)) {
        return true;
      }
    }
    return false;
  }

  public static String getIdAnnotationFieldName(Class<?> clazz) {
    for (Field field : clazz.getDeclaredFields()) {
      if (field.isAnnotationPresent(Id.class)) {
        return field.getName();
      }
    }
    return null;
  }
}
