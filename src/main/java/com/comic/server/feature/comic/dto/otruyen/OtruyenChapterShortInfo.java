package com.comic.server.feature.comic.dto.otruyen;

import com.fasterxml.jackson.annotation.JsonSetter;
import java.io.Serializable;
import lombok.Data;

@Data
public class OtruyenChapterShortInfo implements Serializable {
  @JsonSetter("filename")
  private String filename;

  @JsonSetter("chapter_name")
  private String chapterName;

  @JsonSetter("chapter_title")
  private String chapterTitle;

  @JsonSetter("chapter_api_data")
  private String chapterApiData;
}
