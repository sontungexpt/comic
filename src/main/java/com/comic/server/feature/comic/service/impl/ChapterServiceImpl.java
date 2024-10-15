package com.comic.server.feature.comic.service.impl;

import com.comic.server.exceptions.ResourceNotFoundException;
import com.comic.server.feature.comic.model.chapter.Chapter;
import com.comic.server.feature.comic.repository.ChapterRepository;
import com.comic.server.feature.comic.service.ChapterService;
import org.springframework.stereotype.Service;

@Service
public record ChapterServiceImpl(ChapterRepository chapterRepository) implements ChapterService {

  @Override
  public Chapter getChapterDetailById(String id) {
    return chapterRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(Chapter.class, "id", id));
  }

  @Override
  public Chapter createChapter(Chapter chapter) {
    return chapterRepository.save(chapter);
  }
}
