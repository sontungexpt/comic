package com.comic.server.feature.comic.model.chapter;

import com.comic.server.feature.comic.model.Source;
import java.time.Instant;

public interface Chapter {

  String getId();

  Double getNum();

  Instant getCreatedAt();

  Instant getUpdatedAt();

  String getComicId();

  ChapterType getType();

  String getThumbnailUrl();

  String getName();

  String getDescription();

  Source getOriginalSource();
}
