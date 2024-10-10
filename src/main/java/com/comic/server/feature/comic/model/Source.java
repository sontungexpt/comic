package com.comic.server.feature.comic.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Source {

  @Schema(description = "The URL of the source where the comic was originally fetched")
  private String baseUrl;

  @Schema(description = "The name of the source, such as the website name")
  private String name;

  @Schema(description = "Additional details about the source")
  private String description;
}
