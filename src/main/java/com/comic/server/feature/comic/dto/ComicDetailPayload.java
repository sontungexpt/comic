package com.comic.server.feature.comic.payload;

import com.comic.server.feature.comic.model.Artist;
import com.comic.server.feature.comic.model.Author;
import com.comic.server.feature.comic.model.Comic.Status;
import com.comic.server.feature.comic.model.ComicCategory;
import com.comic.server.feature.comic.model.Source;
import com.comic.server.feature.comic.model.chapter.Chapter;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ComicDetailPayload {

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

  private List<String> tags;

  private List<Chapter> chapters;

  private List<Character> characters;

  private boolean isFollowed;
}
