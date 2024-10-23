package com.comic.server.feature.comic.model;

import com.comic.server.common.model.Sluggable;
import com.comic.server.common.structure.BoundedPriorityQueue;
import com.comic.server.feature.comic.model.chapter.AbstractChapter;
import com.comic.server.feature.comic.model.chapter.ShortInfoChapter;
import com.comic.server.feature.comic.model.thirdparty.SourceName;
import com.comic.server.feature.comic.support.NewChapterComparator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "comics")
@Builder
@JsonIgnoreProperties(
    value = {
      "newChapters",
      "statusUpdatedAt",
    },
    allowGetters = true)
@CompoundIndexes({
  @CompoundIndex(name = "category_status", def = "{'categoryIds': 1, 'status': 1}"),
  @CompoundIndex(
      name = "source_name_id",
      def = "{'originalSource.name': 1, 'originalSource.id': 1}",
      sparse = true),
})
public class Comic implements Sluggable, Serializable {

  public enum Status {
    ONGOING,
    NEW,
    COMMING_SOON,
    COMPLETED,
    UNKNOWN,
    ;
  }

  @Id private String id;

  @Schema(description = "The name of the comic")
  @NotBlank
  private String name;

  private List<String> alternativeNames;

  @Schema(description = "The short summary of the comic")
  @NotBlank
  private String summary;

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
  @JsonIgnore
  private Instant statusUpdatedAt = Instant.now();

  @Default private Integer dailyViews = 0;

  @Default private Instant lastDailyReset = Instant.now();

  @Default private Integer weeklyViews = 0;

  @Default private Instant lastWeeklyReset = Instant.now();

  @Default private Integer monthlyViews = 0;

  @Default private Instant lastMonthlyReset = Instant.now();

  @Default private Integer yearlyViews = 0;

  @Default private Instant lastYearlyReset = Instant.now();

  @Default
  @Schema(description = "The original source from which the comic was fetched")
  private Source originalSource = new Source(SourceName.ROOT);

  @Schema(description = "The authors of the comic")
  private List<@Valid Author> authors;

  @Schema(description = "The artists of the comic")
  private List<@Valid Artist> artists;

  @NotEmpty private List<ObjectId> categoryIds;

  private List<String> tags;

  @JsonGetter("newChapters")
  public List<ShortInfoChapter> getNewChaptersInfo() {
    if (newChapters == null) {
      return List.of();
    }
    return newChapters.stream().sorted((c1, c2) -> c2.getNum().compareTo(c1.getNum())).toList();
  }

  @Setter(AccessLevel.NONE)
  @Getter(AccessLevel.NONE)
  @Schema(description = "The new chapters of the comic")
  private List<ShortInfoChapter> newChapters;

  public void addNewChapter(AbstractChapter chapter) {
    if (newChapters == null) {
      newChapters = List.of(new ShortInfoChapter(chapter));
    } else {
      var newChapters = new BoundedPriorityQueue<>(3, new NewChapterComparator(), this.newChapters);
      if (newChapters.add(chapter)) {
        this.newChapters = newChapters.stream().map((c) -> new ShortInfoChapter(c)).toList();
      }
    }
  }

  @Schema(description = "The characters in the comic")
  private List<@Valid Character> characters;

  @CreatedBy private ObjectId createdBy;

  @CreatedDate private Instant createdAt;

  @LastModifiedBy private String updatedBy;

  @LastModifiedDate private Instant updatedAt;

  @Override
  public String generateSlug() {
    return name;
  }
}
