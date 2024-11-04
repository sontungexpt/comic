package com.comic.server.feature.comic.service.impl;

import com.comic.server.exceptions.ResourceAlreadyInUseException;
import com.comic.server.feature.comic.model.ComicCategory;
import com.comic.server.feature.comic.repository.ComicCategoryRepository;
import com.comic.server.feature.comic.service.ComicCategoryService;
import com.comic.server.utils.DuplicateKeyUtils;
import com.comic.server.utils.DuplicateKeyUtils.KeyValue;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ComicCategoryServiceImpl implements ComicCategoryService {

  private final ComicCategoryRepository comicCategoryRepository;

  @Override
  @Cacheable(value = "comic_categories", unless = "#result == null || #result.isEmpty()")
  public List<ComicCategory> getAllComicCategories() {
    return comicCategoryRepository.findByDeleted(false);
  }

  @Override
  @CacheEvict(value = "comic_categories", allEntries = true)
  public ComicCategory createComicCategory(ComicCategory category) {
    try {
      return comicCategoryRepository.save(category);
    } catch (DuplicateKeyException e) {
      throw new ResourceAlreadyInUseException(ComicCategory.class, "name", category.getName());
    }
  }

  @Override
  @CacheEvict(value = "comic_categories", allEntries = true)
  public List<ComicCategory> createComicCategories(Iterable<ComicCategory> iterable) {
    try {
      return comicCategoryRepository.saveAll(iterable);
    } catch (DuplicateKeyException e) {
      KeyValue keyValue =
          DuplicateKeyUtils.extractDuplicateField(e.getMessage())
              .orElseThrow(
                  () -> new ResourceAlreadyInUseException(ComicCategory.class, "name", "unknown"));

      throw new ResourceAlreadyInUseException(
          ComicCategory.class, keyValue.getKey(), keyValue.getValue());
    }
  }

  @Override
  public long countCategories() {
    return comicCategoryRepository.count();
  }
}
