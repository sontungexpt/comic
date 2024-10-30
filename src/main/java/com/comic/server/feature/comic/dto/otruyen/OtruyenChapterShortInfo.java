package com.comic.server.feature.comic.dto.otruyen;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.io.Serializable;
import lombok.Data;

@Data
@JsonIncludeProperties({"filename", "chapter_name", "chapter_title", "chapter_api_data"})
public class OtruyenChapterShortInfo implements Serializable {

  private String id;

  @JsonSetter("filename")
  private String filename;

  @JsonSetter("chapter_name")
  private String chapterName;

  @JsonSetter("chapter_title")
  private String chapterTitle;

  private String chapterApiData;

  @JsonSetter("chapter_api_data")
  public void setChapterApiData(String chapterName) {
    this.chapterApiData = chapterName;
    this.id = chapterName.substring(chapterName.lastIndexOf("/") + 1);
  }
}
