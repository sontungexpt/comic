package com.comic.server.feature.comic.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "chapters")
@JsonIgnoreProperties(
    value = {
      "id", "comicId",
      "createdAt", "updatedAt"
    },
    allowGetters = true)
public class Chapter {

  @Id private String id;

  private String comicId;

  @NotNull
  @Min(1)
  @Schema(description = "The number of the chapter", example = "1")
  private Integer number;

  @Schema(description = "The name of the chapter", example = "The last hero")
  private String name;

  @Schema(
      description = "The description of the chapter",
      example = "The first chapter of the comic")
  private String description;

  private Source originalSource;

  List<ChapterPage> pages;

  @CreatedDate private Instant createdAt;

  @LastModifiedDate private Instant updatedAt;
}
