package com.comic.server.feature.comic.schedule;

import com.comic.server.feature.comic.model.Comic;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.data.mongodb.core.aggregation.DateOperators.TemporalUnit;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class StatusUpdateSchedule {

  private final MongoTemplate mongoTemplate;

  @Scheduled(cron = "0 0 0 * * *")
  @Async
  public void updateStatuses() {

    log.info("Running status update schedule");

    Aggregation aggregation =
        Aggregation.newAggregation(
            Aggregation.match(Criteria.where("status").is(Comic.Status.NEW)),
            Aggregation.project("statusUpdatedAt", "status", "preOngoingDays", "_id")
                .and(
                    DateOperators.dateOf("statusUpdatedAt")
                        .addValueOf("preOngoingDays", TemporalUnit.from(ChronoUnit.DAYS)))
                .as("calculatedTime"),
            Aggregation.match(Criteria.where("calculatedTime").lt(Instant.now())),
            Aggregation.addFields().addFieldWithValue("status", Comic.Status.ONGOING).build(),
            Aggregation.merge().intoCollection("comics").build());

    var result = mongoTemplate.aggregate(aggregation, Comic.class, Map.class);

    log.info("Status update schedule finished", result);

    // mongoTemplate.updateMulti(
    //     query(where("status").is(Comic.Status.NEW).and("statusUpdatedAt").lt(duration)),
    //     update("status", Comic.Status.ONGOING),
    //     Comic.class);
  }
}
