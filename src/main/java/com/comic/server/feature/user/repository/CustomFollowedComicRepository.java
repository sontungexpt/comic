package com.comic.server.feature.user.repository;

import com.comic.server.feature.comic.dto.ComicDTO;
import com.comic.server.feature.user.model.FollowedComic;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomFollowedComicRepository {

  private final MongoTemplate mongoTemplate;

  public Page<ComicDTO> findByUserId(String userId, Pageable pageable) {

    Aggregation aggregation =
        Aggregation.newAggregation(
            Aggregation.match(Criteria.where("userId").is(new ObjectId(userId))),
            Aggregation.lookup("comics", "comicId", "_id", "comic"),
            Aggregation.unwind("comic"),
            Aggregation.replaceRoot("comic"),
            Aggregation.lookup("comic_categories", "categoryIds", "_id", "categories"),
            Aggregation.sort(pageable.getSort()),
            Aggregation.skip(pageable.getOffset()),
            Aggregation.limit(pageable.getPageSize() + 1));

    var comics =
        mongoTemplate
            .aggregate(aggregation, FollowedComic.class, ComicDTO.class)
            .getMappedResults();

    return new PageImpl<>(comics, pageable, comics.size());
  }
}
