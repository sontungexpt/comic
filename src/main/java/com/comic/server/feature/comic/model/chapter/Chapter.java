package com.comic.server.feature.comic.model.chapter;

import com.comic.server.feature.comic.model.Source;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "chapters")
@JsonIgnoreProperties(
    value = {
      "comicId",
    },
    allowGetters = true)
@JsonTypeInfo(
    include = JsonTypeInfo.As.PROPERTY,
    visible = true,
    use = JsonTypeInfo.Id.NAME,
    property = "type",
    defaultImpl = ComicChapter.class)
@SuperBuilder
public abstract class Chapter {

  @Id private String id;

  @NotBlank private ObjectId comicId;

  public void setComicId(@JsonSetter("comicId") String comicId) {
    this.comicId = new ObjectId(comicId);
  }

  private ChapterType type = ChapterType.COMIC;

  private String thumbnailUrl;

  @NotNull
  @Min(0)
  @Schema(
      description = "The number of the chapter",
      examples = {"1", "2", "2.5"})
  private Double number;

  @Schema(description = "The name of the chapter", example = "The last hero")
  private String name;

  @Schema(
      description = "The description of the chapter",
      example = "The first chapter of the comic")
  private String description;

  private Source originalSource;

  @CreatedDate private Instant createdAt;

  @LastModifiedDate private Instant updatedAt;
}
