package com.comic.server.feature.comic.model.chapter;

import com.comic.server.feature.comic.model.OriginalSource;
import com.comic.server.feature.comic.model.ThirdPartySource;
import com.comic.server.utils.SourceHelper;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ShortInfoChapter implements Chapter {

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

  @Schema(description = "The name of the chapter", example = "The last hero")
  private String name;

  private String description;

  private ThirdPartySource thirdPartySource;

  @Override
  @JsonIgnore
  public ThirdPartySource getThirdPartySource() {
    return thirdPartySource;
  }

  private OriginalSource originalSource;

  @Override
  @JsonGetter("originalSource")
  public OriginalSource getOriginalSource() {
    return SourceHelper.resolveOriginalSource(originalSource, thirdPartySource);
  }

  @JsonIgnore private Instant createdAt;

  private Instant updatedAt;

  @Default private boolean isRead = false;

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
    this.thirdPartySource = chapter.getThirdPartySource();
    this.createdAt = chapter.getCreatedAt();
    this.updatedAt = chapter.getUpdatedAt();
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    else if (!(obj instanceof ShortInfoChapter)) return false;

    ShortInfoChapter that = (ShortInfoChapter) obj;
    if (id != null && that.id != null) {
      return id.equals(that.id);
    } else if (thirdPartySource != null && that.thirdPartySource != null) {
      return thirdPartySource.equals(that.thirdPartySource);
    }
    return false;
  }
}
