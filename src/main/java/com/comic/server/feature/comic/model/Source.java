package com.comic.server.feature.comic.model;

import com.comic.server.feature.comic.model.thirdparty.SourceName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
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

  @Override
  public int hashCode() {
    return Objects.hash(name, id, slug);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj == null || !(obj instanceof Source)) {
      return false;
    }
    Source that = (Source) obj;

    boolean equal = this.name.equals(that.name);

    if (this.id != null && that.id != null) {
      equal = equal && this.id.equals(that.id);
    } else if (this.slug != null && that.slug != null) {
      equal = equal && this.slug.equals(that.slug);
    }

    return equal;
  }
}
