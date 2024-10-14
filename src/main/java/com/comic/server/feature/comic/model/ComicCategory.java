package com.comic.server.feature.comic.model;

import com.comic.server.common.model.Sluggable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
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
public class ComicCategory implements Sluggable {
  @Id private String id;

  @Indexed(unique = true)
  @NotBlank
  private String name;

  private String description;

  @JsonIgnore private boolean deleted = false;

  private String slug;

  public ComicCategory(String name) {
    this.name = name;
  }

  public ComicCategory(String name, String description) {
    this.name = name;
    this.description = description;
  }

  @CreatedDate private Instant createdAt;

  @LastModifiedDate private Instant updatedAt;

  @Override
  public String generateSlug() {
    return name;
  }
}
