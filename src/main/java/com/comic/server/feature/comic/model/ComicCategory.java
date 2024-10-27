package com.comic.server.feature.comic.model;

import com.comic.server.common.model.Sluggable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "comic_categories")
@Builder
@AllArgsConstructor
@Schema(description = "Model for Comic Category")
@JsonIgnoreProperties(
    value = {"deleted"},
    allowGetters = true)
public class ComicCategory implements Sluggable, Serializable {

  @Id private String id;

  @Indexed(unique = true)
  @NotBlank
  @Schema(description = "Name of the category (This is unique field)", example = "Comics")
  private String name;

  @Schema(description = "Description of the category", example = "This is a category for comics")
  private String description;

  @Schema(hidden = true)
  @Default
  @JsonIgnore
  private boolean deleted = false;

  @Schema(hidden = true)
  private String slug;

  public ComicCategory(String name) {
    this.name = name;
  }

  public ComicCategory(String name, String description) {
    this.name = name;
    this.description = description;
  }

  @Schema(hidden = true)
  @CreatedDate
  @JsonIgnore
  private Instant createdAt;

  @Schema(hidden = true)
  @LastModifiedDate
  @JsonIgnore
  private Instant updatedAt;

  @Override
  public String generateSlug() {
    return name;
  }

  @Override
  public boolean isCreatedFromUniqueString() {
    return true;
  }
}
