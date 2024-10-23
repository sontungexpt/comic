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
public class Source implements Serializable {

  @Schema(description = "The id of the document in the source's database")
  private String id;

  @Schema(description = "The slug of the doucment in the source's database")
  private String slug;

  @Schema(description = "The name of the source, such as the website name")
  private SourceName name;

  @Schema(description = "Additional details about the source")
  private String description;

  public Source(SourceName name) {
    this.name = name;
  }
}
