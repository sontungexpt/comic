package com.comic.server.feature.comic.model.chapter;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@JsonTypeName(ChapterType.Fields.NOVEL)
@SuperBuilder
@Getter
@Setter
public class NovelChapter extends Chapter {

  @NotBlank private String content;
}
