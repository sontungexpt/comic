package com.comic.server.feature.comic.dto;

import com.comic.server.common.model.FacetResult;
import com.comic.server.feature.comic.model.Artist;
import com.comic.server.feature.comic.model.Author;
import com.comic.server.feature.comic.model.Character;
import com.comic.server.feature.comic.model.Comic.Status;
import com.comic.server.feature.comic.model.ComicCategory;
import com.comic.server.feature.comic.model.OriginalSource;
import com.comic.server.feature.comic.model.ThirdPartySource;
import com.comic.server.feature.comic.model.chapter.ShortInfoChapter;
import com.comic.server.utils.SourceHelper;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import java.io.Serializable;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ComicDetailDTO implements Serializable {

  private String id;

  private String name;

  private List<String> alternativeNames;

  private String summary;

  private String thumbnailUrl;

  private String slug;

  private Status status;

  private Double rating;

  private ThirdPartySource thirdPartySource;

  @JsonSerialize(using = NullSerializer.class)
  public ThirdPartySource getThirdPartySource() {
    return thirdPartySource;
  }

  private OriginalSource originalSource;

  @JsonGetter("originalSource")
  public OriginalSource getOriginalSource() {
    return SourceHelper.resolveOriginalSource(originalSource, thirdPartySource);
  }

  private List<Author> authors;

  private List<Artist> artists;

  private List<ComicCategory> categories;

  private List<String> tags;

  private List<ShortInfoChapter> newChapters;

  private Page<ShortInfoChapter> chapters;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  @JsonIgnore
  private transient List<FacetResult<ShortInfoChapter>> chaptersFacets;

  @JsonIgnore
  public final FacetResult<ShortInfoChapter> getChaptersFacet() {
    if (chaptersFacets == null || chaptersFacets.isEmpty()) {
      return null;
    }
    return chaptersFacets.get(0);
  }

  public void pageChapters(Pageable pageable) {
    var chaptersFacet = getChaptersFacet();
    if (chaptersFacet != null) {
      chapters =
          new PageImpl<ShortInfoChapter>(
              chaptersFacet.getDatas(), pageable, chaptersFacet.getCount("totalChapters"));
    }
  }

  private List<Character> characters;

  private boolean isFollowed;
}
