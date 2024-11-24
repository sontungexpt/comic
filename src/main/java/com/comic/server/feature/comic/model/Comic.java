package com.comic.server.feature.comic.model;

import com.comic.server.annotation.AutoSlugify;
import com.comic.server.common.model.Sluggable;
import com.comic.server.common.structure.BoundedPriorityQueue;
import com.comic.server.common.structure.BoundedPriorityQueue.Strategy;
import com.comic.server.feature.comic.model.chapter.Chapter;
import com.comic.server.feature.comic.model.chapter.ShortInfoChapter;
import com.comic.server.feature.comic.support.NewChapterComparator;
import com.comic.server.feature.user.model.User;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

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
      types = {"string"},
      examples = {"6718cb6a65f0056b56c6682a"})
  @NotEmpty
  private List<ObjectId> categoryIds;

  @Schema(
      description = "The tags of the comic to help with recommendations",
      examples = {"action", "adventure"})
  private List<String> tags;

  @JsonGetter("newChapters")
  @Schema(hidden = true)
  public List<ShortInfoChapter> getNewChaptersInfo() {
    if (newChapters == null) return List.of();
    return newChapters.stream().sorted((c1, c2) -> c2.getNum().compareTo(c1.getNum())).toList();
  }

  @Schema(hidden = true)
  private Collection<ShortInfoChapter> newChapters;

  private BoundedPriorityQueue<Chapter> buildChapterPriorityQueue(boolean updateIfExists) {
    return new BoundedPriorityQueue<>(
        3,
        new NewChapterComparator(),
        this.newChapters,
        updateIfExists ? Strategy.UPDATE_IF_EXISTS : Strategy.UNIQUE);
  }

  private List<ShortInfoChapter> buildShortInfoChapters(Collection<? extends Chapter> chapters) {
    return chapters.stream().map(ShortInfoChapter::new).toList();
  }

  public boolean addNewChapter(@NonNull Chapter chapter, boolean updateIfExists) {
    if (this.newChapters == null) {
      this.newChapters = List.of(new ShortInfoChapter(chapter));
      return true;
    }
    var newChapters = buildChapterPriorityQueue(updateIfExists);
    if (newChapters.add(chapter)) {
      this.newChapters = buildShortInfoChapters(newChapters);
      return true;
    }
    return false;
  }

  public boolean addNewChapter(Chapter chapter) {
    return addNewChapter(chapter, false);
  }

  public boolean addNewChapters(
      @Nullable Collection<? extends Chapter> chapters, boolean updateIfExists) {
    if (chapters == null || chapters.isEmpty()) return false;
    else if (this.newChapters == null) {
      this.newChapters = new ArrayList<>(buildShortInfoChapters(chapters));
      return true;
    }
    var newChapters = buildChapterPriorityQueue(updateIfExists);
    if (newChapters.addAll(chapters)) {
      this.newChapters = buildShortInfoChapters(newChapters);
      return true;
    }
    return false;
  }

  public boolean addNewChapters(@Nullable Collection<? extends Chapter> chapters) {
    return addNewChapters(chapters, false);
  }

  public boolean removeNewChapter(String chapterId) {
    if (newChapters == null) return false;
    return newChapters.removeIf((c) -> c.getId().equals(chapterId));
  }

  public boolean removeNewChapter(Chapter chapter) {
    if (newChapters == null) return false;
    return newChapters.remove(new ShortInfoChapter(chapter));
  }

  @Schema(description = "The last time new chapters were checked", hidden = true)
  @Default
  private Instant lastNewChaptersCheckedAt = Instant.now();

  @Schema(
      description = "The characters in the comic",
      type = "array",
      exampleClasses = {Character.class})
  private List<@Valid Character> characters;

  @Schema(
      description = "The translators of the comic",
      type = "array",
      exampleClasses = {Translator.class})
  private List<@Valid Translator> translators;

  @Schema(hidden = true)
  @JsonIgnore
  @CreatedDate
  private Instant createdAt;

  @Schema(hidden = true)
  @JsonIgnore
  @LastModifiedDate
  private Instant updatedAt;

  @Schema(hidden = true)
  @CreatedBy
  private ObjectId ownerId;

  @Schema(hidden = true)
  @JsonIgnore
  @LastModifiedBy
  private ObjectId lastModifiedBy;

  @JsonIgnore
  public boolean isOwner(String userId) {
    return ownerId != null && ownerId.toHexString().equals(userId);
  }

  @JsonIgnore
  @Schema(hidden = true, description = "The users who can update the comic")
  private Set<ObjectId> editorIds;

  @JsonIgnore
  public final Set<ObjectId> getEditorIds() {
    if (editorIds == null) editorIds = new HashSet<>();
    editorIds.add(ownerId);
    return new HashSet<>(editorIds);
  }

  @JsonIgnore
  public boolean couldBeEditBy(String userId) {
    return editorIds != null
        && editorIds.stream().anyMatch((editorId) -> editorId.toHexString().equals(userId));
  }

  @JsonIgnore
  public boolean couldBeEditBy(User user) {
    Assert.notNull(user, "User must not be null");
    return couldBeEditBy(user.getId());
  }

  public boolean addEditor(ObjectId userId) {
    if (editorIds == null) editorIds = new HashSet<>();
    return editorIds.add(userId);
  }

  public boolean addEditor(String id) {
    return addEditor(new ObjectId(id));
  }

  public boolean removeEditor(String id) {
    return editorIds != null && editorIds.removeIf((userId) -> userId.toHexString().equals(id));
  }

  public boolean removeEditor(ObjectId userId) {
    return editorIds != null && editorIds.remove(userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, thirdPartySource);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    else if (!(obj instanceof Comic)) return false;
    final Comic that = (Comic) obj;
    if (that != null && this.id != null) {
      return that.id.equals(this.id);
    } else if (thirdPartySource != null && that.thirdPartySource != null) {
      return thirdPartySource.equals(that.thirdPartySource);
    }
    return false;
  }
}
