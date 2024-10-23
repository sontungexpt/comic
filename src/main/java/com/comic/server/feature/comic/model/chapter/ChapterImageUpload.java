package com.comic.server.feature.comic.model.chapter;

import com.cloudinary.StoredFile;

public class ChapterImageUpload extends StoredFile {

  private String chapterId;

  public String getChapterId() {
    return chapterId;
  }

  public void setChapterId(String chapterId) {
    this.chapterId = chapterId;
  }

  @Override
  public String toString() {
    return "ChapterImageUpload [chapterId=" + chapterId + "]";
  }
}
