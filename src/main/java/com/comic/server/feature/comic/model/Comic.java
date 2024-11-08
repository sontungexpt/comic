package com.comic.server.feature.comic.model;

import com.comic.server.annotation.AutoSlugify;
import com.comic.server.common.model.Sluggable;
import com.comic.server.common.structure.BoundedPriorityQueue;
import com.comic.server.feature.comic.model.chapter.Chapter;
import com.comic.server.feature.comic.model.chapter.ShortInfoChapter;
import com.comic.server.feature.comic.support.NewChapterComparator;
import com.comic.server.utils.SourceHelper;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Null;
import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
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
      "id",
      "slug",
      "lastNewChaptersCheckedAt",
      "statusUpdatedAt",
      "statusUpdatedAt",
      "dailyViews",
      "lastDailyReset",
      "thirdPartySource",
      "weeklyViews",
      "newChapters",
      "updatedAt",
      "updatedBy",
      "createdAt",
      "createdBy",
    },
    allowGetters = true)
@CompoundIndexes({
  @CompoundIndex(name = "category_status", def = "{'categoryIds': 1, 'status': 1}"),
  @CompoundIndex(
      name = "source_name_slug",
      def = "{'thirdPartySource.name': 1, 'thirdPartySource.slug': 1}",
      unique = true,
      partialFilter = "{ 'thirdPartySource.slug': { $exists: true } }"),
})
@NoArgsConstructor
@AllArgsConstructor
public class Comic implements Sluggable<String>, Serializable {

  public enum Status {
    ONGOING,
    NEW,
    COMMING_SOON,
    COMPLETED,
    UNKNOWN,
    ;
  }

  @Id
  @Schema(hidden = true)
  @Null
  private String id;

  @Schema(description = "The name of the comic", example = "One Piece")
  @NotBlank
  private String name;

  @Schema(
      description = "The alternative names of the comic",
      examples = {"Vua hải tặc", "ワンピース"})
  private List<String> alternativeNames;

  @Schema(description = "The short summary of the comic", example = "The story of Luffy")
  @NotBlank
  private String summary;

  @Schema(description = "The URL of the comic intro image", hidden = true)
  private String thumbnailUrl;

  @Schema(hidden = true)
  @AutoSlugify(fields = "name")
  private String slug;

  @Schema(description = "The status of the comic", defaultValue = "NEW")
  @Default
  private Status status = Status.NEW;

  public void setStatus(Status newStatus) {
    statusUpdatedAt = Instant.now();
    status = newStatus;
  }

  @Default
  @Setter(AccessLevel.NONE)
  @JsonIgnore
  @Schema(hidden = true)
  private Instant statusUpdatedAt = Instant.now();

  @Schema(description = "The number of days the comic has been ongoing", example = "7")
  @Default
  @Min(1)
  private int preOngoingDays = 7;

  @Schema(description = "The number of views the comic has received", hidden = true)
  @Default
  private Integer dailyViews = 0;

  @Schema(description = "The last time the daily views were reset", hidden = true)
  @Default
  @JsonIgnore
  private Instant lastDailyReset = Instant.now();

  @Schema(
      description = "The number of views the comic has received in the last week",
      hidden = true)
  @Default
  private Integer weeklyViews = 0;

  @Schema(description = "The last time the weekly views were reset", hidden = true)
  @Default
  @JsonIgnore
  private Instant lastWeeklyReset = Instant.now();

  @Schema(
      description = "The number of views the comic has received in the last month",
      hidden = true)
  @Default
  private Integer monthlyViews = 0;

  @Schema(description = "The last time the monthly views were reset", hidden = true)
  @Default
  @JsonIgnore
  private Instant lastMonthlyReset = Instant.now();

  @Schema(
      description = "The number of views the comic has received in the last year",
      hidden = true)
  @Default
  private Integer yearlyViews = 0;

  @Schema(description = "The last time the yearly views were reset", hidden = true)
  @Default
  @JsonIgnore
  private Instant lastYearlyReset = Instant.now();

  @Schema(
      description = "The original source of the comic",
      example =
          "{\"name\":\"MangaDex\",\"description\":\"MangaDex\", \"link\":\"https://mangadex.org\"}",
      requiredMode = RequiredMode.NOT_REQUIRED)
  private OriginalSource originalSource;

  @JsonGetter("originalSource")
  public OriginalSource getOriginalSource() {
    return SourceHelper.resolveOriginalSource(originalSource, thirdPartySource);
  }

  @Default
  @Schema(description = "The original source from which the comic was fetched", hidden = true)
  @JsonIgnore
  private ThirdPartySource thirdPartySource = ThirdPartySource.defaultSource();

  @Schema(
      description = "The authors of the comic",
      exampleClasses = {Author.class})
  private List<@Valid Author> authors;

  @Schema(
      description = "The artists of the comic",
      exampleClasses = {Artist.class})
  private List<@Valid Artist> artists;

  @Schema(
      description = "The category ids of the comic",
      examples = {"6718cb6a65f0056b56c6682a"})
  @NotEmpty
  private List<@com.comic.server.validation.annotation.ObjectId ObjectId> categoryIds;

  @Schema(
      description = "The tags of the comic to help with recommendations",
      examples = {"action", "adventure"})
  private List<String> tags;

  @JsonGetter("newChapters")
  @Schema(hidden = true)
  public List<ShortInfoChapter> getNewChaptersInfo() {
    if (newChapters == null) {
      return List.of();
    }
    return newChapters.stream().sorted((c1, c2) -> c2.getNum().compareTo(c1.getNum())).toList();
  }

  @Schema(hidden = true)
  private Collection<ShortInfoChapter> newChapters;

  public boolean addNewChapter(Chapter chapter) {
    if (newChapters == null) {
      newChapters = List.of(new ShortInfoChapter(chapter));
      return true;
    }

    var newChapters =
        new BoundedPriorityQueue<>(3, new NewChapterComparator(), this.newChapters, true);

    if (newChapters.add(chapter)) {
      this.newChapters = newChapters.stream().map((c) -> new ShortInfoChapter(c)).toList();
      return true;
    }
    return false;
  }

  @Schema(description = "The last time new chapters were checked", hidden = true)
  @Default
  private Instant lastNewChaptersCheckedAt = Instant.now();

  @Schema(description = "The characters in the comic")
  private List<@Valid Character> characters;

  @Schema(hidden = true)
  @CreatedBy
  private ObjectId createdBy;

  @Schema(hidden = true)
  @JsonIgnore
  @CreatedDate
  private Instant createdAt;

  @Schema(hidden = true)
  @JsonIgnore
  @LastModifiedBy
  private ObjectId updatedBy;

  @Schema(hidden = true)
  @JsonIgnore
  @LastModifiedDate
  private Instant updatedAt;

  @Override
  public int hashCode() {
    return Objects.hash(id, thirdPartySource);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    final Comic other = (Comic) obj;
    if (other != null && this.id != null) {
      return other.id.equals(this.id);
    } else if (thirdPartySource != null && other.thirdPartySource != null) {
      return thirdPartySource.equals(other.thirdPartySource);
    }
    return false;
  }
}
