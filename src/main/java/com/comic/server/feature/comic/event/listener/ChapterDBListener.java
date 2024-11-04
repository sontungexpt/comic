package com.comic.server.feature.comic.event.listener;

import com.comic.server.exceptions.ResourceNotFoundException;
import com.comic.server.feature.comic.model.Comic;
import com.comic.server.feature.comic.model.chapter.AbstractChapter;
import com.comic.server.feature.comic.repository.ChapterRepository;
import com.comic.server.feature.comic.repository.ComicRepository;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class ChapterDBListener extends AbstractMongoEventListener<AbstractChapter> {

  private final ComicRepository comicRepository;
  private final ChapterRepository chapterRepository;

  @Override
  public void onAfterSave(AfterSaveEvent<AbstractChapter> event) {
    AbstractChapter chapter = event.getSource();

    Comic comic =
        comicRepository
            .findById(chapter.getComicId())
            .orElseThrow(
                () -> {
                  CompletableFuture.runAsync(() -> chapterRepository.deleteById(chapter.getId()));
                  throw new ResourceNotFoundException(Comic.class, "id", chapter.getComicId());
                });

    log.info("Chapter {} added to comic {}", chapter.getId(), comic.getId());
    comic.addNewChapter(chapter);
    comicRepository.save(comic);
  }
}
