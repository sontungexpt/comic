package com.comic.server.feature.comic.service;

import com.comic.server.feature.comic.model.ComicCategory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ComicCategoryService {

  ComicCategory createComicCategory(ComicCategory category);

  List<ComicCategory> createComicCategories(Iterable<ComicCategory> iterable);

  Page<ComicCategory> getAllComicCategories(Pageable pageable);

  List<ComicCategory> getAllComicCategories();

  long countCategories();
}
