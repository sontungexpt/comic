package com.comic.server.feature.comic.schedule;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.comic.server.feature.comic.model.Comic;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public record StatusUpdateSchedule(MongoTemplate mongoTemplate) {

  @Scheduled(cron = "0 0 0 * * *")
  public void updateStatuses() {
    Instant duration = Instant.now().minus(10, ChronoUnit.DAYS);

    mongoTemplate.updateMulti(
        query(where("status").is(Comic.Status.NEW).and("statusUpdatedAt").lt(duration)),
        update("status", Comic.Status.ONGOING),
        Comic.class);
  }
}
