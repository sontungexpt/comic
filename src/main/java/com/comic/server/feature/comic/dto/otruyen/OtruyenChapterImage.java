package com.comic.server.feature.comic.dto.otruyen;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

@Data
@JsonIncludeProperties({"image_page", "image_file"})
public class OtruyenChapterImage {

  @JsonSetter("image_page")
  private int imagePage;

  @JsonSetter("image_file")
  private String imageFile;
}
