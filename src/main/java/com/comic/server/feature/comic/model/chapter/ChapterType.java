package com.comic.server.feature.comic.model.chapter;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants
public enum ChapterType {
  @FieldNameConstants.Include
  COMIC,
  @FieldNameConstants.Include
  NOVEL,
}
