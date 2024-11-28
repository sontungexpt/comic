package com.comic.server.feature.comic.repository;

import com.comic.server.common.model.FacetResult;
import com.comic.server.exceptions.ResourceNotFoundException;
import com.comic.server.feature.comic.dto.ComicDTO;
import com.comic.server.feature.comic.dto.ComicDetailDTO;
import com.comic.server.feature.comic.dto.FacetComicDTOResult;
import com.comic.server.feature.comic.model.Comic;
import com.comic.server.feature.user.model.User;
import com.comic.server.feature.user.service.FollowedComicService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ComicDetailRepository {
  private final MongoTemplate mongoTemplate;

  private final FollowedComicService followedComicService;

  private Aggregation buildAggregation(Criteria criteria, Pageable pageable) {
    return Aggregation.newAggregation(
        Aggregation.match(criteria),
        Aggregation.lookup("comic_categories", "categoryIds", "_id", "categories"),
        Aggregation.facet(Aggregation.count().as("totalComics"))
            .as(FacetResult.getCountFacetName())
            .and(
                Aggregation.sort(pageable.getSort()),
                Aggregation.skip(pageable.getOffset()),
                Aggregation.limit(pageable.getPageSize()),
                Aggregation.project(ComicDTO.class))
            .as(FacetResult.getDataFacetName()));
  }

  private Page<ComicDTO> executeComicAggregation(Criteria criteria, Pageable pageable) {
    Aggregation aggregation = buildAggregation(criteria, pageable);

    var facetResult =
        mongoTemplate
            .aggregate(aggregation, Comic.class, FacetComicDTOResult.class)
            .getUniqueMappedResult();

    List<ComicDTO> comics = facetResult.getDatas();
    long totalCount = facetResult.getCount("totalComics");
    return new PageImpl<>(comics, pageable, totalCount);
  }

  public Page<ComicDTO> findAllWithCategories(Pageable pageable, List<String> filterCategoryIds) {

    var criteria = new Criteria();
    // Apply category filter if category IDs are provided
    if (filterCategoryIds != null && !filterCategoryIds.isEmpty()) {
      criteria
          .and("categoryIds")
          .all(filterCategoryIds.stream().filter(ObjectId::isValid).map(ObjectId::new).toList());
    }

    return executeComicAggregation(criteria, pageable);
  }

  public ComicDetailDTO findComicDetail(String comicId, Pageable pageable, User user) {
    Document sortChaptersDocument = new Document("num", 1);
    pageable
        .getSort()
        .forEach(
            order -> {
              sortChaptersDocument.append(order.getProperty(), order.isAscending() ? 1 : -1);
            });
    AggregationOperation lookupWithSortedChapters =
        new AggregationOperation() {
          @Override
          public Document toDocument(AggregationOperationContext context) {
            Document lookup =
                new Document(
                    "$lookup",
                    new Document()
                        .append("from", "chapters")
                        .append("let", new Document("comicId", "$_id"))
                        .append(
                            "pipeline",
                            List.of(
                                context.getMappedObject(
                                    new Document(
                                        "$match",
                                        new Document(
                                            "$expr",
                                            new Document(
                                                "$eq", List.of("$comicId", "$$comicId"))))),
                                context.getMappedObject(
                                    new Document(
                                        "$facet",
                                        new Document(
                                                FacetResult.getCountFacetName(),
                                                List.of(new Document("$count", "totalChapters")))
                                            .append(
                                                FacetResult.getDataFacetName(),
                                                List.of(
                                                    new Document("$sort", sortChaptersDocument),
                                                    new Document("$skip", pageable.getOffset()),
                                                    new Document(
                                                        "$limit", pageable.getPageSize())))),
                                    FacetResult.class)))
                        .append("as", "chaptersFacets"));
            return lookup;
          }
        };
    Aggregation aggregation;

    aggregation =
        Aggregation.newAggregation(
            Aggregation.match(Criteria.where("_id").is(comicId)),
            Aggregation.lookup("comic_categories", "categoryIds", "_id", "categories"),
            lookupWithSortedChapters);

    var comic =
        mongoTemplate
            .aggregate(aggregation, Comic.class, ComicDetailDTO.class)
            .getUniqueMappedResult();

    if (comic == null) {
      throw new ResourceNotFoundException(Comic.class, "id", comicId);
    }

    comic.pageChapters(pageable);

    if (user != null) {
      comic.setFollowed(followedComicService.isUserFollowingComic(user.getId(), comicId));
    }

    return comic;
  }

  public Page<ComicDTO> findComicDetailByKeyword(String keyword, Pageable pageable) {
    var criteria = new Criteria();
    criteria.orOperator(
        Criteria.where("name").regex(keyword, "i"),
        Criteria.where("alternativeNames").regex(keyword, "i"));

    return executeComicAggregation(criteria, pageable);
  }

  public long countComicDTOs(List<String> filterCategoryIds) {
    Query query = new Query();

    if (!filterCategoryIds.isEmpty()) {
      query.addCriteria(
          new Criteria()
              .and("categoryIds")
              .all(filterCategoryIds.stream().map(ObjectId::new).toList()));
    }

    return mongoTemplate.count(query, Comic.class);
  }
}
