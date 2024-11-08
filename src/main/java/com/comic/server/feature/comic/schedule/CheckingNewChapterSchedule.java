package com.comic.server.feature.comic.schedule;

import com.comic.server.feature.comic.model.Comic;
import com.comic.server.feature.comic.model.chapter.ShortInfoChapter;
import com.comic.server.feature.comic.model.thirdparty.SourceName;
import com.comic.server.feature.comic.service.impl.OtruyenComicServiceImpl;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CheckingNewChapterSchedule {

  private final OtruyenComicServiceImpl otruyenComicService;
  private final MongoTemplate mongoTemplate;

  @Async
  @Scheduled(cron = "0 */30 13-17 * * MON-FRI")
  @Scheduled(cron = "0 */30 0-7 * * *")
  public void syncNewChaptersFromOtruyen() {

    log.info("Start sync new chapters from Otruyen");

    var query =
        new Query()
            .addCriteria(Criteria.where("thirdPartySource.name").is(SourceName.OTRUYEN))
            .with(Sort.by(Sort.Direction.ASC, "lastNewChaptersCheckedAt"))
            .limit(15);

    List<Comic> comics = mongoTemplate.find(query, Comic.class);
    BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Comic.class);

    Instant now = Instant.now();

    for (Comic comic : comics) {
      try {
        List<ShortInfoChapter> chapters = otruyenComicService.getChaptersByComic(comic);
        if (chapters == null || chapters.isEmpty()) {
          continue;
        }

        // get three lastest chapter at the end of list
        int size = chapters.size();
        List<ShortInfoChapter> lastestChapters =
            size > 3 ? chapters.subList(size - 3, size) : chapters;

        Update update = new Update();
        if (comic.addNewChapters(lastestChapters)) {
          log.info("Sync new chapters for comic: {}", comic.getName());
          update.set("newChapters", comic.getNewChapters());
        }
        update.set("lastNewChaptersCheckedAt", now);
        bulkOps.updateOne(Query.query(Criteria.where("id").is(comic.getId())), update);
      } catch (Exception e) {
        log.error("Error when sync new chapters for comic {}", comic.getName(), e);
      }
    }

    try {
      bulkOps.execute();
    } catch (Exception e) {
      log.error("Error when sync new chapters for comics", e);
    }

    log.info("End sync new chapters from Otruyen");
  }
}
