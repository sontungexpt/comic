package com.comic.server.feature.comic.model.chapter;

import com.comic.server.feature.comic.model.Source;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

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
@Data
@Getter
@Setter
public abstract class Chapter {

  @Id private String id;

  @JsonSetter("_id")
  public void setId(ObjectId id) {
    this.id = id.toHexString();
  }

  @JsonGetter("id")
  public String getId() {
    return id;
  }

  @NotNull private ObjectId comicId;

  public String getComicId() {
    return comicId.toHexString();
  }

  public void setComicId(@JsonSetter("comicId") ObjectId comicId) {

    this.comicId = comicId;
  }

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
  private Double num;

  @JsonGetter("chapter")
  public String getChapter() {
    return "Chapter " + num;
  }

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
