package com.comic.server.feature.comic.dto.otruyen;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.List;
import lombok.Data;

@Data
@JsonIncludeProperties({
  "_id",
  "comic_name",
  "chapter_name",
  "chapter_title",
  "chapter_path",
  "chapter_image"
})
public class OtruyenChapterDetail {
  @JsonSetter("_id")
  private String id;

  @JsonSetter("comic_name")
  private String comicName;

  @JsonSetter("chapter_name")
  private String chapterName;

  @JsonSetter("chapter_title")
  private String chapterTitle;

  @JsonSetter("chapter_path")
  private String chapterPath;

  @JsonSetter("chapter_image")
  private List<OtruyenChapterImage> chapterImages;
}
