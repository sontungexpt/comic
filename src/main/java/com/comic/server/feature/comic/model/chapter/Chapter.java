package com.comic.server.feature.comic.model.chapter;

import com.comic.server.feature.comic.model.OriginalSource;
import com.comic.server.feature.comic.model.ThirdPartySource;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.io.Serializable;
import java.time.Instant;

@JsonIgnoreProperties(
    value = {
      "id",
      "chapter",
      "createdAt",
      "updatedAt",
      "createdBy",
      "updatedBy",
    },
    allowGetters = true)
public interface Chapter extends Serializable {

  @Schema(
      description = "The unique identifier of the chapter",
      example = "60f3b3b3b3b3b3b3b3b3b333",
      hidden = true)
  String getId();

  Double getNum();

  @JsonGetter("chapter")
  default String getChapter() {
    return "Chapter " + getNum();
  }

  @Schema(
      description = "The date and time when the chapter was created",
      example = "2021-07-19T00:00:00Z",
      required = true)
  Instant getCreatedAt();

  @Schema(
      description = "The date and time when the chapter was last updated",
      example = "2021-07-19T00:00:00Z",
      required = true)
  Instant getUpdatedAt();

  String getComicId();

  ChapterType getType();

  String getThumbnailUrl();

  String getName();

  String getDescription();

  @JsonSerialize(using = NullSerializer.class)
  ThirdPartySource getThirdPartySource();

  @Schema(
      description = "The original source of the chapter",
      example =
          "{\"name\":\"MangaDex\",\"description\":\"MangaDex\", \"link\":\"https://mangadex.org\"}",
      requiredMode = RequiredMode.NOT_REQUIRED)
  OriginalSource getOriginalSource();

  int hashCode();

  boolean equals(Object o);
}
