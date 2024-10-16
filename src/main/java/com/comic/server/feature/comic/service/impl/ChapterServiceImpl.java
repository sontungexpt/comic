package com.comic.server.feature.comic.service.impl;

import com.comic.server.exceptions.ResourceAlreadyInUseException;
import com.comic.server.exceptions.ResourceNotFoundException;
import com.comic.server.feature.comic.model.chapter.AbstractChapter;
import com.comic.server.feature.comic.repository.ChapterRepository;
import com.comic.server.feature.comic.service.ChapterService;
import java.util.Map;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public record ChapterServiceImpl(ChapterRepository chapterRepository) implements ChapterService {

  @Override
  @Transactional
  public AbstractChapter getChapterDetailById(String id) {
    return chapterRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(AbstractChapter.class, "id", id));
  }

  @Override
  @Transactional
  public AbstractChapter createChapter(AbstractChapter chapter) {

    try {
      return chapterRepository.save(chapter);
    } catch (DuplicateKeyException e) {
      throw new ResourceAlreadyInUseException(
          AbstractChapter.class, Map.of("comicId", chapter.getComicId(), "num", chapter.getNum()));
    }
  }
}
