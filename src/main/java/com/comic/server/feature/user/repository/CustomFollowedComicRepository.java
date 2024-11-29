package com.comic.server.feature.user.repository;

import com.comic.server.common.model.FacetResult;
import com.comic.server.feature.comic.dto.ComicDTO;
import com.comic.server.feature.comic.dto.FacetComicDTOResult;
import com.comic.server.feature.comic.model.Comic;
import com.comic.server.feature.user.model.FollowedComic;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
            Aggregation.facet(Aggregation.count().as("totalComics"))
                .as(FacetResult.getCountFacetName())
                .and(
                    Aggregation.sort(Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("_id"))),
                    Aggregation.skip(pageable.getOffset()),
                    Aggregation.limit(pageable.getPageSize()),
                    Aggregation.lookup(
                        mongoTemplate.getCollectionName(Comic.class), "comicId", "_id", "comics"),
                    Aggregation.project().and("comics").arrayElementAt(0).as("comic"),
                    Aggregation.replaceRoot("comic"),
                    Aggregation.lookup("comic_categories", "categoryIds", "_id", "categories"))
                .as(FacetResult.getDataFacetName()));

    var result =
        mongoTemplate
            .aggregate(aggregation, FollowedComic.class, FacetComicDTOResult.class)
            .getUniqueMappedResult();

    return new PageImpl<>(result.getDatas(), pageable, result.getCount("totalComics"));
  }

  public Page<ComicDTO> searchFollowedComic(String keyword, String userId, Pageable pageable) {

    Criteria findindKeywordCriteria = new Criteria();
    findindKeywordCriteria.orOperator(
        Criteria.where("name").regex(keyword, "i"),
        Criteria.where("alternativeNames").regex(keyword, "i"));

    Aggregation aggregation =
        Aggregation.newAggregation(
            Aggregation.match(Criteria.where("userId").is(new ObjectId(userId))),
            Aggregation.facet(Aggregation.count().as("totalComics"))
                .as(FacetResult.getCountFacetName())
                .and(
                    Aggregation.sort(Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("_id"))),
                    Aggregation.lookup(
                        mongoTemplate.getCollectionName(Comic.class), "comicId", "_id", "comics"),
                    Aggregation.project().and("comics").arrayElementAt(0).as("comic"),
                    Aggregation.replaceRoot("comic"),
                    Aggregation.match(findindKeywordCriteria),
                    Aggregation.skip(pageable.getOffset()),
                    Aggregation.limit(pageable.getPageSize()),
                    Aggregation.lookup("comic_categories", "categoryIds", "_id", "categories"))
                .as(FacetResult.getDataFacetName()));

    var result =
        mongoTemplate
            .aggregate(aggregation, FollowedComic.class, FacetComicDTOResult.class)
            .getUniqueMappedResult();

    return new PageImpl<>(result.getDatas(), pageable, result.getCount("totalComics"));
  }
}
