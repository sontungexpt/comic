package com.comic.server.feature.comic.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "comics")
@JsonIgnoreProperties(
    value = {"id", "createdAt", "updatedAt"},
    allowGetters = true)
public class Comic {
  @Id private String id;

  @Schema(description = "The name of the comic")
  @NotBlank
  private String name;

  List<String> originalNames;

  @Schema(description = "The short description of the comic")
  @NotBlank
  private String description;

  @Schema(description = "The URL of the comic intro image")
  private String imageUrl;

  private String slug;

  private List<Author> authorNames;

  private List<Artist> artistNames;

  private List<String> categories;

  private List<String> tags;

  @DBRef private List<Character> characters;

  @CreatedDate private Instant createdAt;

  @LastModifiedDate private Instant updatedAt;
}
