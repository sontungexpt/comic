package com.comic.server.feature.comic.model;

import com.comic.server.common.payload.Sluggable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "comic_categories")
@JsonIgnoreProperties(
    value = {"id", "createdAt", "updatedAt"},
    allowGetters = true)
@Getter
@Setter
@NoArgsConstructor
public class ComicCategory implements Sluggable {
  @Id private String id;

  @NotBlank private String name;

  private String description;

  public ComicCategory(String name) {
    this.name = name;
  }

  public ComicCategory(String name, String description) {
    this.name = name;
    this.description = description;
  }

  private String slug;

  @CreatedDate private Instant createdAt;

  @LastModifiedDate private Instant updatedAt;

  @Override
  public String createSlugFrom() {
    return name;
  }
}
