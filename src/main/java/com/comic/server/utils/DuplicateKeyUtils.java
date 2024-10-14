package com.comic.server.utils;

import java.util.Optional;

public class DuplicateKeyUtils {

  public static class KeyValue {
    private String key;
    private String value;

    public KeyValue(String key, String value) {
      this.key = key;
      this.value = value;
    }

    public String getKey() {
      return key;
    }

    public String getValue() {
      return value;
    }
  }

  public static Optional<KeyValue> extractDuplicateField(String message) {

    String[] parts = message.split("dup key: ");

    if (parts.length > 1) {
      String keyDetails = parts[1];

      int startIndex = keyDetails.indexOf("{");
      int endIndex = keyDetails.indexOf("}");

      if (startIndex >= 0 && endIndex > startIndex) {
        String keyValue = keyDetails.substring(startIndex + 1, endIndex).trim();

        String[] keyValueParts = keyValue.split(":");
        if (keyValueParts.length == 2) {
          String key = keyValueParts[0].trim();
          String value = keyValueParts[1].trim();

          value = value.replaceAll("^\"|\"$", "").replaceAll("^'|'$", "");

          return Optional.of(new KeyValue(key, value));
        }
      }
    }
    return Optional.empty();
  }
}
