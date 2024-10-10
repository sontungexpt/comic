package com.comic.server.feature.comic.service.impl;

import com.comic.server.feature.comic.model.ComicCategory;
import com.comic.server.feature.comic.repository.ComicCategoryRepository;
import com.comic.server.feature.comic.service.ComicCategoryService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public record ComicCategoryServiceImpl(ComicCategoryRepository comicCategoryRepository)
    implements ComicCategoryService {

  @Override
  public Page<ComicCategory> getAllComicCategories(Pageable pageable) {
    return comicCategoryRepository.findAll(pageable);
  }

  @Override
  public List<ComicCategory> getAllComicCategories() {
    return comicCategoryRepository.findAll();
  }

  @Override
  public ComicCategory createComicCategory(ComicCategory category) {
    return comicCategoryRepository.save(category);
  }

  @Override
  public List<ComicCategory> createComicCategories(Iterable<ComicCategory> iterable) {
    return comicCategoryRepository.saveAll(iterable);
  }

  @Override
  public long countCategories() {
    return comicCategoryRepository.count();
  }
}
