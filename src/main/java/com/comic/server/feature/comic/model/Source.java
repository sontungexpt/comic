package com.comic.server.feature.comic.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Source {

  private String idFromSource;

  private String slugFromSource;

  @Schema(description = "The URL of the source where the comic was originally fetched")
  private String baseUrl;

  @Schema(description = "The name of the source, such as the website name")
  private String name;

  @Schema(description = "Additional details about the source")
  private String description;
}
