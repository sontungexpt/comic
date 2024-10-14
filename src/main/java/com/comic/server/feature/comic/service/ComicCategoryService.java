package com.comic.server.feature.comic.service;

import com.comic.server.feature.comic.model.ComicCategory;
import java.util.List;

public interface ComicCategoryService {

  ComicCategory createComicCategory(ComicCategory category);

  List<ComicCategory> createComicCategories(Iterable<ComicCategory> iterable);

  List<ComicCategory> getAllComicCategories();

  long countCategories();
}
