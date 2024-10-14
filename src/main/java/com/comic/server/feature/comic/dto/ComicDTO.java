package com.comic.server.feature.comic.dto;

import com.comic.server.feature.comic.model.Artist;
import com.comic.server.feature.comic.model.Author;
import com.comic.server.feature.comic.model.Comic.Status;
import com.comic.server.feature.comic.model.ComicCategory;
import com.comic.server.feature.comic.model.Source;
import com.comic.server.feature.comic.model.chapter.Chapter;
// import com.comic.server.feature.comic.model.chapter.Chapter;
import com.comic.server.feature.user.model.FollowedComic;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComicDTO {

  private String id;

  private String name;

  List<String> originalNames;

  private String summary;

  private String thumbnailUrl;

  private String slug;

  private Status status;

  private Double rating;

  private Source originalSource;

  private List<Author> authors;

  private List<Artist> artists;

  private List<ComicCategory> categories;

  @JsonIgnore private List<FollowedComic> followedUsers;

  private List<String> tags;

  private List<Chapter> newChapters;

  private List<Character> characters;

  // private boolean isFollowed;

  @JsonGetter("followed")
  public boolean isFollowed() {
    return followedUsers != null && !followedUsers.isEmpty();
  }
}
