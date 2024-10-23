package com.comic.server.feature.comic.model.chapter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants
@Schema(description = "Chapter type")
public enum ChapterType {
  @FieldNameConstants.Include
  @Schema(description = "Comic")
  COMIC,
  @FieldNameConstants.Include
  @Schema(description = "Novel")
  NOVEL,
}
