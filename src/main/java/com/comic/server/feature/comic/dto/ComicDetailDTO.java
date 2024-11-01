package com.comic.server.feature.comic.dto;

import com.comic.server.feature.comic.model.Artist;
import com.comic.server.feature.comic.model.Author;
import com.comic.server.feature.comic.model.Comic.Status;
import com.comic.server.feature.comic.model.ComicCategory;
import com.comic.server.feature.comic.model.OriginalSource;
import com.comic.server.feature.comic.model.ThirdPartySource;
import com.comic.server.feature.comic.model.chapter.ShortInfoChapter;
import com.comic.server.utils.SourceHelper;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

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

  private List<Character> characters;

  // NOTE: Unimplemented field
  private boolean isFollowed;
}
