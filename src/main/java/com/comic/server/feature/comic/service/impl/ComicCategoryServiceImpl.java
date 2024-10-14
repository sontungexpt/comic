package com.comic.server.feature.comic.service.impl;

import com.comic.server.exceptions.ResourceAlreadyInUseException;
import com.comic.server.feature.comic.model.ComicCategory;
import com.comic.server.feature.comic.repository.ComicCategoryRepository;
import com.comic.server.feature.comic.service.ComicCategoryService;
import com.comic.server.utils.ConsoleUtils;
import com.comic.server.utils.DuplicateKeyUtils;
import com.comic.server.utils.DuplicateKeyUtils.KeyValue;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public record ComicCategoryServiceImpl(ComicCategoryRepository comicCategoryRepository)
    implements ComicCategoryService {

  @Override
  public List<ComicCategory> getAllComicCategories() {
    return comicCategoryRepository.findByDeleted(false);
  }

  @Override
  public ComicCategory createComicCategory(ComicCategory category) {
    try {
      return comicCategoryRepository.save(category);
    } catch (DuplicateKeyException e) {
      throw new ResourceAlreadyInUseException(ComicCategory.class, "name", category.getName());
    }
  }

  @Override
  public List<ComicCategory> createComicCategories(Iterable<ComicCategory> iterable) {
    try {
      return comicCategoryRepository.saveAll(iterable);
    } catch (DuplicateKeyException e) {
      KeyValue keyValue =
          DuplicateKeyUtils.extractDuplicateField(e.getMessage())
              .orElseThrow(
                  () -> new ResourceAlreadyInUseException(ComicCategory.class, "name", "unknown"));

      ConsoleUtils.prettyPrint(keyValue.getKey());
      ConsoleUtils.prettyPrint(keyValue.getValue());

      throw new ResourceAlreadyInUseException(
          ComicCategory.class, keyValue.getKey(), keyValue.getValue());
    }
  }

  @Override
  public long countCategories() {
    return comicCategoryRepository.count();
  }
}
