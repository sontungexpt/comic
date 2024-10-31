package com.comic.server.feature.comic.dto;

import com.comic.server.feature.comic.model.Artist;
import com.comic.server.feature.comic.model.Author;
import com.comic.server.feature.comic.model.Character;
import com.comic.server.feature.comic.model.Comic.Status;
import com.comic.server.feature.comic.model.ComicCategory;
import com.comic.server.feature.comic.model.Source;
import com.comic.server.feature.comic.model.chapter.ShortInfoChapter;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComicDTO implements Serializable {

  private String id;

  private String name;

  private List<String> alternativeNames;

  private String summary;

  private String thumbnailUrl;

  private String slug;

  private Status status;

  private Source originalSource;

  private List<Author> authors;

  private List<Artist> artists;

  private List<ComicCategory> categories;

  private List<String> tags;

  private Iterable<ShortInfoChapter> newChapters;

  private List<Character> characters;

  @Override
  public int hashCode() {
    return Objects.hash(id, originalSource);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    final ComicDTO other = (ComicDTO) obj;
    if (other != null && this.id != null) {
      return other.id.equals(this.id);
    } else if (originalSource != null && other.originalSource != null) {
      return originalSource.equals(other.originalSource);
    }
    return false;
  }
}
