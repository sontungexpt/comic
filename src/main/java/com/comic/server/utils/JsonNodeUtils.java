package com.comic.server.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import org.springframework.lang.NonNull;

public class JsonNodeUtils {
  /**
   * Removes fields from a JsonNode based on a array of field paths.
   *
   * <p>Field paths are specified in dot-separated format for nested fields. For example,
   * "field1.nestedField" will remove "nestedField" from the "field1" object.
   *
   * @param node The JsonNode from which fields will be removed.
   * @param fieldPaths A array of field paths to be removed. If null or empty, no fields will be
   *     removed.
   * @return The modified JsonNode with specified fields removed.
   * @throws IllegalArgumentException If requestNode is null.
   */
  public static JsonNode removeFields(@NonNull JsonNode node, String... fieldPaths) {
    if (node == null) {
      throw new IllegalArgumentException("requestNode cannot be null");
    } else if (fieldPaths.length == 0) {
      return node;
    } else if (!node.isObject()) {
      return node;
    }

    for (String path : fieldPaths) {
      String[] tree = path.split("\\.");
      int length = tree.length;
      if (length == 0) {
        continue;
      }
      JsonNode currentNode = node;
      for (int i = 0; i < length - 1; i++) {
        if ((currentNode = currentNode.get(tree[i])) == null) {
          break;
        }
      }

      if (currentNode instanceof ObjectNode) {
        ((ObjectNode) currentNode).remove(tree[length - 1]);
      }
    }
    return node;
  }

  /**
   * Removes fields from a JsonNode based on a collection of field paths.
   *
   * <p>Field paths are specified in dot-separated format for nested fields. For example,
   * "field1.nestedField" will remove "nestedField" from the "field1" object.
   *
   * @param node The JsonNode from which fields will be removed.
   * @param fieldPaths A collection of field paths to be removed. If null or empty, no fields will
   *     be removed.
   * @return The modified JsonNode with specified fields removed.
   * @throws IllegalArgumentException If requestNode is null.
   */
  public static JsonNode removeFields(JsonNode node, Collection<String> fieldPaths) {
    return removeFields(node, fieldPaths.toArray(new String[0]));
  }

  public static JsonNode removeFields(JsonNode node, Map<String, Object> fieldTree) {
    if (fieldTree == null || fieldTree.isEmpty()) {
      return node;
    } else if (!node.isObject()) {
      return node;
    }
    return removeFieldsRecursive(node, fieldTree);
  }

  @SuppressWarnings("unchecked")
  private static JsonNode removeFieldsRecursive(
      JsonNode node, Map<String, Object> ignoredFieldsTree) {
    if (ignoredFieldsTree.isEmpty()) {
      return null;
    }
    for (Map.Entry<String, Object> entry : ignoredFieldsTree.entrySet()) {
      String field = entry.getKey();
      JsonNode childNode = node.get(field);
      if (childNode == null) {
        continue;
      }
      Object value = entry.getValue();
      if (value instanceof Map) {
        removeFieldsRecursive(childNode, (Map<String, Object>) value);
      } else if ((value instanceof Boolean) && (Boolean) value && node instanceof ObjectNode) {
        ((ObjectNode) node).remove(field);
      } else if (node instanceof ObjectNode) {
        ((ObjectNode) node).remove(field);
      }
    }
    return node;
  }

  public static boolean isTypeCompatible(Class<?> fieldType, JsonNode fieldNode) {
    if (fieldNode.isNull()) {
      return fieldType == null || fieldType.isAssignableFrom(Void.class);
    } else if (fieldNode.isTextual()) {
      return fieldType.equals(String.class); // So sánh với String
    } else if (fieldNode.isInt()) {
      return fieldType.equals(Integer.TYPE) || fieldType.equals(Integer.class); // int hoặc Integer
    } else if (fieldNode.isLong()) {
      return fieldType.equals(Long.TYPE) || fieldType.equals(Long.class); // long hoặc Long
    } else if (fieldNode.isDouble()) {
      return fieldType.equals(Double.TYPE) || fieldType.equals(Double.class); // double hoặc Double
    } else if (fieldNode.isShort()) {
      return fieldType.equals(Short.TYPE) || fieldType.equals(Short.class); // short hoặc Short
    } else if (fieldNode.isFloat()) {
      return fieldType.equals(Float.TYPE) || fieldType.equals(Float.class); // float hoặc Float
    } else if (fieldNode.isBoolean()) {
      return fieldType.equals(Boolean.TYPE) || fieldType.equals(Boolean.class);
    } else if (fieldNode.isBigDecimal()) {
      return fieldType.equals(BigDecimal.class);
    } else if (fieldNode.isBigInteger()) {
      return fieldType.equals(BigInteger.class);
    } else if (fieldNode.isBinary()) {
      return fieldType.equals(byte[].class);
    } else if (fieldNode.isArray()) {
      return fieldType.isArray() || Collection.class.isAssignableFrom(fieldType);
    } else if (fieldNode.isObject()) {
      return Map.class.isAssignableFrom(fieldType) || !fieldType.isPrimitive();
    }
    return false;
  }
}
