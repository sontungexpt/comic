package com.comic.server.feature.comic.model.chapter;

import com.comic.server.feature.comic.model.OriginalSource;
import com.comic.server.feature.comic.model.ThirdPartySource;
import com.comic.server.utils.SourceHelper;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mongodb.lang.NonNull;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;
import lombok.Builder.Default;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chapters")
@JsonIgnoreProperties(
    value = {
      "id",
      "comicId",
      "thirdPartySource",
      "createdAt",
      "updatedAt",
      "createdBy",
      "updatedBy"
    },
    allowGetters = true)
@JsonTypeInfo(
    include = JsonTypeInfo.As.PROPERTY,
    visible = true,
    use = JsonTypeInfo.Id.NAME,
    property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = ComicChapter.class, name = ChapterType.Fields.COMIC),
  @JsonSubTypes.Type(value = NovelChapter.class, name = ChapterType.Fields.NOVEL)
})
@SuperBuilder
@Data
@Getter
@Setter
@CompoundIndexes({
  @CompoundIndex(
      name = "comicId_num",
      def = "{'comicId': 1, 'num': 1}",
      unique = true,
      sparse = true)
})
public abstract class AbstractChapter implements Chapter {

  @Schema(
      description = "The unique identifier of the chapter",
      example = "60f3b3b3b3b3b3b3b3b3b3b3",
      hidden = true)
  @Id
  private String id;

  public void setId(ObjectId id) {
    this.id = id.toHexString();
  }

  @JsonGetter("id")
  public String getId() {
    return id;
  }

  @Schema(
      description = "The unique identifier of the comic",
      example = "60f3b3b3b3b3b3b3b3b3b3",
      hidden = true)
  @NonNull
  private ObjectId comicId;

  @JsonIgnore
  public ObjectId getComicIdAsObjectId() {
    return comicId;
  }

  public String getComicId() {
    if (comicId == null) {
      return null;
    }
    return comicId.toHexString();
  }

  public void setComicId(ObjectId comicId) {
    this.comicId = comicId;
  }

  public void setComicId(@JsonSetter String comicId) {
    this.comicId = new ObjectId(comicId);
  }

  @Schema(description = "The type of the chapter", example = "COMIC")
  private ChapterType type;

  @Schema(
      description = "The URL of the thumbnail of the chapter",
      example = "https://example.com/thumbnail.jpg")
  private String thumbnailUrl;

  @NotNull
  @Min(0)
  @Schema(
      description = "The number of the chapter",
      examples = {"1", "2", "2.5"})
  private Double num;

  @Schema(description = "The name of the chapter", example = "The last hero")
  private String name;

  @Schema(
      description = "The description of the chapter",
      example = "The first chapter of the comic")
  private String description;

  @Schema(
      description = "The source of the chapter, if the user get the chapter from a other source",
      example = "ROOT",
      allowableValues = {"ROOT", "OTRUYEN", "MANGADEX"})
  private OriginalSource originalSource;

  @Schema(
      description = "The source of the chapter",
      example = "ROOT",
      hidden = true,
      allowableValues = {"ROOT", "OTRUYEN", "MANGADEX"})
  @Default
  private ThirdPartySource thirdPartySource = ThirdPartySource.defaultSource();

  @Override
  @JsonIgnore
  public ThirdPartySource getThirdPartySource() {
    return thirdPartySource;
  }

  @Override
  @JsonGetter("originalSource")
  public OriginalSource getOriginalSource() {
    return SourceHelper.resolveOriginalSource(originalSource, thirdPartySource);
  }

  @Schema(hidden = true)
  @CreatedDate
  private Instant createdAt;

  @JsonGetter("updatedDate")
  public Instant getUpdatedDate() {
    return updatedAt;
  }

  @Schema(hidden = true)
  @LastModifiedDate
  private Instant updatedAt;

  public AbstractChapter(AbstractChapter chapter) {
    this.id = chapter.id;
    this.comicId = chapter.comicId;
    this.type = chapter.type;
    this.thumbnailUrl = chapter.thumbnailUrl;
    this.num = chapter.num;
    this.name = chapter.name;
    this.description = chapter.description;
    this.originalSource = chapter.originalSource;
    this.thirdPartySource = chapter.thirdPartySource;
    this.createdAt = chapter.createdAt;
    this.updatedAt = chapter.updatedAt;
  }

  public AbstractChapter() {}

  @Override
  public int hashCode() {
    return Objects.hash(id, thirdPartySource);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    } else if (!(obj instanceof AbstractChapter)) {
      return false;
    }

    AbstractChapter that = (AbstractChapter) obj;
    if (id != null && that.id == null) {
      return id.equals(that.id);
    } else if (thirdPartySource != null && that.thirdPartySource == null) {
      return thirdPartySource.equals(that.thirdPartySource);
    }
    return false;
  }
}
