package com.comic.server.feature.comic.schedule;

import com.comic.server.feature.comic.model.Comic;
import com.comic.server.feature.comic.model.chapter.ShortInfoChapter;
import com.comic.server.feature.comic.model.thirdparty.SourceName;
import com.comic.server.feature.comic.repository.ComicRepository;
import com.comic.server.feature.comic.service.impl.OtruyenComicServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SyncNewChapterSchedule {

  private final ComicRepository comicRepository;
  private final OtruyenComicServiceImpl otruyenComicService;

  @Async
  @Scheduled(cron = "0 0 0 * * *")
  public void syncNewChaptersFromOtruyen() {

    List<Comic> comics = comicRepository.findByThirdPartySourceName(SourceName.OTRUYEN);

    for (Comic comic : comics) {
      try {
        List<ShortInfoChapter> chapters = otruyenComicService.getChaptersByComic(comic);
        if (chapters == null || chapters.isEmpty()) {
          return;
        }

        // get three lastest chapter at the end of list
        int size = chapters.size();
        List<ShortInfoChapter> lastestChapters =
            size > 3 ? chapters.subList(size - 3, size) : chapters;

        for (ShortInfoChapter chapter : lastestChapters) {
          comic.addNewChapter(chapter);
        }
      } catch (Exception e) {
        log.error("Error when sync new chapters for comic: {}", comic.getName(), e);
      }
    }

    comicRepository.saveAll(comics);
  }
}
