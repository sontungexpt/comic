package com.comic.server.feature.comic.repository;

import com.comic.server.feature.comic.dto.ComicDTO;
import com.comic.server.feature.comic.dto.ComicDetailDTO;
import com.comic.server.feature.comic.model.Comic;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ComicDetailRepository {
  private final MongoTemplate mongoTemplate;
  private final ChapterRepository chapterRepository;
  // private final FollowedComicRepository followedComicRepository;
  private final ObjectMapper objectMapper;

  private Aggregation buildAggregation(Criteria criteria, Pageable pageable) {
    return Aggregation.newAggregation(
        Aggregation.match(criteria),
        Aggregation.lookup("comic_categories", "categoryIds", "_id", "categories"),
        Aggregation.facet(Aggregation.count().as("totalComics"))
            .as("countFacet")
            .and(
                Aggregation.sort(pageable.getSort()),
                Aggregation.skip(pageable.getOffset()),
                Aggregation.limit(pageable.getPageSize()),
                Aggregation.project(ComicDTO.class))
            .as("dataFacet"));
  }

  private ComicDTO mapToComicDTO(Map<String, Object> object) {
    ComicDTO comic = objectMapper.convertValue(object, ComicDTO.class);
    comic.setId(object.get("_id").toString());
    return comic;
  }

  @SuppressWarnings("unchecked")
  private int getTotalCount(Map<String, Object> results, String countFacetName) {
    return results.get(countFacetName) == null
            || ((List<Map<String, Object>>) results.get(countFacetName)).isEmpty()
        ? 0
        : (int) ((List<Map<String, Object>>) results.get(countFacetName)).get(0).get("totalComics");
  }

  private Page<ComicDTO> executeComicAggregation(Criteria criteria, Pageable pageable) {
    Aggregation aggregation = buildAggregation(criteria, pageable);
    var results =
        mongoTemplate.aggregate(aggregation, Comic.class, Map.class).getUniqueMappedResult();

    @SuppressWarnings("unchecked")
    List<ComicDTO> comics =
        ((List<Map<String, Object>>) results.get("dataFacet"))
            .stream().map(object -> mapToComicDTO(object)).toList();

    @SuppressWarnings("unchecked")
    int totalCount = getTotalCount(results, "countFacet");
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

  public ComicDetailDTO findComicDetail(String comicId, Pageable pageable) {

    Aggregation aggregation =
        Aggregation.newAggregation(
            Aggregation.match(Criteria.where("_id").is(comicId)),
            Aggregation.lookup("comic_categories", "categoryIds", "_id", "categories"));

    var comics =
        mongoTemplate
            .aggregate(aggregation, Comic.class, ComicDetailDTO.class)
            .getUniqueMappedResult();

    comics.setChapters(
        chapterRepository.findByComicIdOrderByNumDesc(new ObjectId(comicId), pageable));

    // comics.setFollowed(followedComicRepository.existsByUserIdAndComicId(userId, comicId));
    return comics;
  }

  public Page<ComicDTO> findComicDetailByKeyword(String keyword, Pageable pageable) {
    var criteria = new Criteria();
    criteria.orOperator(
        Criteria.where("name").regex(keyword, "i"),
        Criteria.where("alternativeNames").regex(keyword, "i"));

    return executeComicAggregation(criteria, pageable);
  }
}
