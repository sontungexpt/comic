package com.comic.server.feature.comic.model.chapter;

import com.comic.server.feature.comic.model.Source;
import com.fasterxml.jackson.annotation.JsonGetter;
import java.io.Serializable;
import java.time.Instant;

public interface Chapter extends Serializable {

  String getId();

  Double getNum();

  @JsonGetter("chapter")
  default String getChapter() {
    return "Chapter " + getNum();
  }

  Instant getCreatedAt();

  Instant getUpdatedAt();

  String getComicId();

  ChapterType getType();

  String getThumbnailUrl();

  String getName();

  String getDescription();

  Source getOriginalSource();
}
