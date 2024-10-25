package com.comic.server.feature.comic.model;

import com.comic.server.feature.comic.model.thirdparty.SourceName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema
public class Source implements Serializable {

  @Schema(description = "The id of the document in the source's database", hidden = true)
  private String id;

  @Schema(description = "The slug of the doucment in the source's database", hidden = true)
  private String slug;

  @Schema(description = "The name of the source, such as the website name", example = "Website")
  private SourceName name;

  @Schema(description = "Additional details about the source", example = "This is a website")
  private String description;

  public Source(SourceName name) {
    this.name = name;
  }
}
