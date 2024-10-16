package com.comic.server.feature.comic.model.chapter;

import com.comic.server.feature.comic.model.Source;
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
      "comicId",
      "originalSource",
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

  @Id private String id;

  public void setId(ObjectId id) {
    this.id = id.toHexString();
  }

  @JsonGetter("id")
  public String getId() {
    return id;
  }

  @NonNull private ObjectId comicId;

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

  private ChapterType type;

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

  @Default private Source originalSource = Source.builder().name("Comic").build();

  @CreatedDate private Instant createdAt;

  @JsonGetter("updatedDate")
  public Instant getUpdatedDate() {
    return updatedAt;
  }

  @LastModifiedDate private Instant updatedAt;

  public AbstractChapter(AbstractChapter chapter) {
    this.id = chapter.id;
    this.comicId = chapter.comicId;
    this.type = chapter.type;
    this.thumbnailUrl = chapter.thumbnailUrl;
    this.num = chapter.num;
    this.name = chapter.name;
    this.description = chapter.description;
    this.originalSource = chapter.originalSource;
    this.createdAt = chapter.createdAt;
    this.updatedAt = chapter.updatedAt;
  }

  public AbstractChapter() {}

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof AbstractChapter)) {
      return false;
    }
    AbstractChapter chapter = (AbstractChapter) obj;
    return id.equals(chapter.id);
  }
}
