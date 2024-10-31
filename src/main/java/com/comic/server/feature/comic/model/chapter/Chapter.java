package com.comic.server.feature.comic.model.chapter;

import com.comic.server.feature.comic.model.Source;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
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
      example = "60f3b3b3b3b3b3b3b3b3b3",
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

  Source getOriginalSource();
}
