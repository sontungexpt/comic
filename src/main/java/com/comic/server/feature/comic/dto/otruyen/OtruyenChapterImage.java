package com.comic.server.feature.comic.dto.otruyen;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

@Data
public class OtruyenChapterImage {

  @JsonSetter("image_page")
  private int imagePage;

  @JsonSetter("image_file")
  private String imageFile;
}
