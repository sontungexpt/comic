package com.comic.server.feature.comic.dto;

import com.comic.server.feature.comic.model.Artist;
import com.comic.server.feature.comic.model.Author;
import com.comic.server.feature.comic.model.Comic.Status;
import com.comic.server.feature.comic.model.ComicCategory;
import com.comic.server.feature.comic.model.Source;
import com.comic.server.feature.comic.model.chapter.ShortInfoChapter;
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

  private Source originalSource;

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
