package com.comic.server.feature.comic.model;

import com.comic.server.common.model.Sluggable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "comics")
@JsonIgnoreProperties(
    value = {
      "id",
      "slug",
      "statusUpdatedAt",
      "createdAt",
      "updatedAt",
    },
    allowGetters = true)
@Builder
public class Comic implements Sluggable {

  public enum Status {
    ONGOING,
    NEW,
    COMMING_SOON,
    COMPLETED,
  }

  @Id private String id;

  @Schema(description = "The name of the comic")
  @NotBlank
  private String name;

  List<String> originalNames;

  @Schema(description = "The short description of the comic")
  @NotBlank
  private String description;

  @Schema(description = "The URL of the comic intro image")
  private String thumbnailUrl;

  private String slug;

  @Schema(description = "The status of the comic")
  @Default
  private Status status = Status.NEW;

  public void setStatus(Status newStatus) {
    statusUpdatedAt = Instant.now();
    status = newStatus;
  }

  @Default
  @Setter(AccessLevel.NONE)
  private Instant statusUpdatedAt = Instant.now();

  @Schema(description = "The original source was the comic was fetch")
  private Source originalSource;

  private List<Author> authors;

  private List<Artist> artists;

  private List<String> categoryIds;

  private List<String> tags;

  private Chapter lastChapter;

  private List<Character> characters;

  @CreatedDate private Instant createdAt;

  @LastModifiedDate private Instant updatedAt;

  @Override
  public String createSlugFrom() {
    return name;
  }
}
