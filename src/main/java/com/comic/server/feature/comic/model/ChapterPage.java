package com.comic.server.feature.comic.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChapterPage {

  private int number;

  private String imagePath;

  public String getChapterImageFileName() {
    return "chapter-" + number + ".jpg";
  }
}
