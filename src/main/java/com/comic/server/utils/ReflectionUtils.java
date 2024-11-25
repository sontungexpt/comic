package com.comic.server.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReflectionUtils {

  public static boolean isPrimitiveTypeOrString(Field field) {
    Class<?> type = field.getType();
    return type.isPrimitive() || type.equals(String.class);
  }

  public static List<Field> getDeclaredFieldsRecursive(Class<?> clazz) {
    List<Field> fields = new ArrayList<>();

    // to make sure that the filed in
    // child priority
    HashMap<String, Boolean> existsFieldName = new HashMap<>();

    while (clazz != null && clazz != Object.class) {
      Field[] declaredFields = clazz.getDeclaredFields();
      for (Field field : declaredFields) {
        if (!existsFieldName.containsKey(field.getName())) {
          fields.add(field);
          existsFieldName.put(field.getName(), true);
        }
      }
      clazz = clazz.getSuperclass();
    }
    return fields;
  }

  public static Field getDeclaredField(Class<?> clazz, String fieldName) {
    return org.springframework.util.ReflectionUtils.findField(clazz, fieldName);
  }
}
