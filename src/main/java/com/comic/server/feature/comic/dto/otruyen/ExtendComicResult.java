package com.comic.server.feature.comic.dto.otruyen;

import com.comic.server.feature.comic.model.Comic;
import com.comic.server.feature.comic.model.ComicCategory;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ExtendComicResult {
  private Comic comic;
  private List<ComicCategory> categories;

  public ExtendComicResult(Comic comic, List<ComicCategory> categories) {
    this.comic = comic;
    this.categories = categories;
  }
}
