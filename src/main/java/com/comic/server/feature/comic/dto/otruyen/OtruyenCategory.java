package com.comic.server.feature.comic.dto.otruyen;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Data;

@Data
public class OtruyenCategory implements Serializable {
  @JsonProperty("id")
  private String id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("slug")
  private String slug;
}
