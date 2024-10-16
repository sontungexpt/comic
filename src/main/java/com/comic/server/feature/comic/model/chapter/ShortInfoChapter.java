package com.comic.server.feature.comic.model.chapter;

import com.comic.server.feature.comic.model.Source;
import com.fasterxml.jackson.annotation.JsonGetter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
public class ShortInfoChapter implements Chapter, Serializable {

  private String id;

  private ObjectId comicId;

  @JsonGetter("comicId")
  public String getComicId() {
    if (comicId == null) {
      return null;
    }
    return comicId.toHexString();
  }

  private ChapterType type;

  private String thumbnailUrl;

  private Double num;

  @JsonGetter("chapter")
  public String getChapter() {
    return "Chapter " + num;
  }

  @Schema(description = "The name of the chapter", example = "The last hero")
  private String name;

  private String description;

  private Source originalSource;

  @Transient private Instant createdAt;

  private Instant updatedAt;

  @JsonGetter("updatedDate")
  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public ShortInfoChapter(Chapter chapter) {
    this.id = chapter.getId();
    String comicId = chapter.getComicId();
    this.comicId = comicId == null ? null : new ObjectId(comicId);
    this.type = chapter.getType();
    this.thumbnailUrl = chapter.getThumbnailUrl();
    this.num = chapter.getNum();
    this.name = chapter.getName();
    this.description = chapter.getDescription();
    this.originalSource = chapter.getOriginalSource();
    this.createdAt = chapter.getCreatedAt();
    this.updatedAt = chapter.getUpdatedAt();
  }

  public ShortInfoChapter() {}
}
