package com.comic.server.feature.comic.dto.otruyen;

import com.fasterxml.jackson.annotation.JsonSetter;
import java.io.Serializable;
import lombok.Data;

@Data
public class OtruyenCategory implements Serializable {
  @JsonSetter("id")
  private String id;

  @JsonSetter("name")
  private String name;

  @JsonSetter("slug")
  private String slug;
}
