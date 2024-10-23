package com.comic.server.feature.comic.model.thirdparty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Source name")
public enum SourceName {
  @Schema(description = "otruyencc")
  OTRUYEN("OTRUYEN"),

  @Schema(description = "my comic list")
  ROOT("ROOT");

  private final String name;

  SourceName(String name) {
    this.name = name;
  }

  @JsonValue
  public String getName() {
    return name;
  }

  @JsonCreator
  public static SourceName fromName(String name) {
    name = name.toUpperCase();
    for (SourceName sourceName : SourceName.values()) {
      if (sourceName.getName().equals(name)) {
        return sourceName;
      }
    }
    return null;
  }

  public static SourceName fromNameOrDefault(String name, SourceName defaultSourceName) {
    SourceName sourceName = fromName(name);
    return sourceName == null ? defaultSourceName : sourceName;
  }

  @Override
  public String toString() {
    return name;
  }
}
