package com.comic.server.feature.comic.repository;

import com.comic.server.feature.comic.dto.ComicDTO;
import com.comic.server.feature.comic.model.Comic;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomComicRepository {
  private final MongoTemplate mongoTemplate;

  public Page<ComicDTO> findAllWithCategories(Pageable pageable) {
    Aggregation aggregation =
        Aggregation.newAggregation(
            Aggregation.lookup("comic_categories", "categoryIds", "_id", "categories"),
            Aggregation.sort(pageable.getSort()),
            Aggregation.skip(pageable.getOffset()),
            Aggregation.limit(pageable.getPageSize() + 1));

    var comics =
        mongoTemplate.aggregate(aggregation, Comic.class, ComicDTO.class).getMappedResults();

    return new PageImpl<>(comics, pageable, comics.size());
  }
}
