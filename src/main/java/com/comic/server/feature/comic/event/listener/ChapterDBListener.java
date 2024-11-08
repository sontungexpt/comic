package com.comic.server.feature.comic.event.listener;

import com.comic.server.exceptions.ResourceNotFoundException;
import com.comic.server.feature.comic.model.Comic;
import com.comic.server.feature.comic.model.chapter.AbstractChapter;
import com.comic.server.feature.comic.repository.ChapterRepository;
import com.comic.server.feature.comic.repository.ComicRepository;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class ChapterDBListener extends AbstractMongoEventListener<AbstractChapter> {

  private final ComicRepository comicRepository;
  private final ChapterRepository chapterRepository;

  private Comic getComic(String comicId, String chapterId) {
    return comicRepository
        .findById(comicId)
        .orElseThrow(
            () -> {
              CompletableFuture.runAsync(() -> chapterRepository.deleteById(chapterId));
              throw new ResourceNotFoundException(Comic.class, "id", comicId);
            });
  }

  @Override
  @Async
  public void onAfterSave(AfterSaveEvent<AbstractChapter> event) {
    AbstractChapter chapter = event.getSource();
    Comic comic = getComic(chapter.getComicId(), chapter.getId());
    log.info("Chapter {} added to comic {}", chapter.getId(), comic.getId());
    comic.addNewChapter(chapter, true);
    comicRepository.save(comic);
  }

  @Override
  public void onAfterDelete(AfterDeleteEvent<AbstractChapter> event) {
    Document document = event.getSource();
    String chapterId = String.valueOf(document.get("_id"));
    String comicId = document.getString("comicId");
    Comic comic = getComic(comicId, chapterId);
    log.info("Chapter {} deleted from comic {}", chapterId, comicId);
    comic.removeNewChapter(chapterId);
    comicRepository.save(comic);
  }
}
