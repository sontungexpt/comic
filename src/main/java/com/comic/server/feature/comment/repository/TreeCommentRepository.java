package com.comic.server.feature.comment.repository;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.comic.server.common.model.FacetResult;
import com.comic.server.feature.comment.dto.CommentGroup;
import com.comic.server.feature.comment.dto.CommentResponse;
import com.comic.server.feature.comment.model.Comment;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TreeCommentRepository {

  private final MongoTemplate mongoTemplate;

  /**
   * Increase total replies of a comment
   *
   * @param parentId parent comment id
   * @param count number of replies to increase
   */
  public void increaseTotalReplies(String parentId, int count) {
    log.info("Increase total replies: {}", parentId + " by " + count);
    mongoTemplate.updateFirst(
        query(where("id").is(parentId)), new Update().inc("totalReplies", count), Comment.class);
  }

  /**
   * Increase total replies of a comment asynchronously
   *
   * @param parentId parent comment id
   * @param count number of replies to increase
   */
  @Async
  public void increaseTotalRepliesAsync(String parentId, int count) {
    log.info("Increase total replies async: {}", parentId + " by " + count);
    increaseTotalReplies(parentId, count);
  }

  /**
   * Decrease total replies of a comment
   *
   * @param parentId parent comment id
   * @param count number of replies to decrease
   */
  public void decreaseTotalReplies(String parentId, int count) {
    log.info("Decrease total replies: {}", parentId + " by " + count);
    mongoTemplate.updateFirst(
        query(where("id").is(parentId)), new Update().inc("totalReplies", -count), Comment.class);
  }

  /**
   * Decrease total replies of a comment asynchronously
   *
   * @param parentId parent comment id
   * @param count number of replies to decrease
   */
  @Async
  public void decreaseTotalRepliesAsync(String parentId, int count) {
    log.info("Decrease total replies async: {}", parentId + " by " + count);
    decreaseTotalReplies(parentId, count);
  }

  /**
   * Increase total replies of multiple comments
   *
   * @param parentIds parent comment ids
   * @param count number of replies to increase
   */
  public void increaseTotalReplies(Collection<String> parentIds, int count) {
    log.info("Increase total replies: {}", parentIds + " by " + count);
    mongoTemplate.updateMulti(
        query(where("id").in(parentIds)), new Update().inc("totalReplies", count), Comment.class);
  }

  /**
   * Increase total replies of multiple comments asynchronously
   *
   * @param parentIds parent comment ids
   * @param count number of replies to increase
   */
  @Async
  public void increaseTotalRepliesAsync(Collection<String> parentIds, int count) {
    log.info("Increase total replies async: {}", parentIds + " by " + count);
    increaseTotalReplies(parentIds, count);
  }

  /**
   * Decrease total replies of multiple comments
   *
   * @param parentIds parent comment ids
   * @param count number of replies to decrease
   */
  public void decreaseTotalReplies(Collection<String> parentIds, int count) {
    log.info("Decrease total replies: {}", parentIds + " by " + count);
    mongoTemplate.updateMulti(
        query(where("id").in(parentIds)), new Update().inc("totalReplies", -count), Comment.class);
  }

  /**
   * Decrease total replies of multiple comments asynchronously
   *
   * @param parentIds parent comment ids
   * @param count number of replies to decrease
   */
  @Async
  public void decreaseTotalRepliesAsync(Collection<String> parentIds, int count) {
    log.info("Decrease total replies async: {}", parentIds + " by " + count);
    decreaseTotalReplies(parentIds, count);
  }

  /**
   * Find top level replies of a comment
   *
   * @param parentId parent comment id
   * @param pageable pagination
   * @return page of comments
   */
  public Page<CommentResponse> findTopLevelReplies(String parentId, Pageable pageable) {
    Criteria criteria = where("parentId").is(new ObjectId(parentId));
    return aggregateFlatComments(criteria, pageable);
  }

  /**
   * Find top level comments of a comic or chapter
   *
   * @param comicId comic id
   * @param chapterId chapter id
   * @param parentId parent comment id
   * @param pageable pagination
   * @return page of comments
   */
  public Page<?> findTopLevelComments(
      @NonNull String comicId,
      @Nullable String chapterId,
      @Nullable String parentId,
      @NonNull Pageable pageable) {

    Criteria criteria = buildFilterCommentCriteria(comicId, chapterId, parentId);

    if (chapterId == null && parentId == null) {
      return aggregateGroupedComments(criteria, pageable);
    }
    return aggregateFlatComments(criteria, pageable);
  }

  /**
   * Find a comment by id
   *
   * @param id comment id
   * @return comment
   */
  private List<AggregationOperation> addAuthorLookupAndPagination(Pageable pageable) {
    return new ArrayList<AggregationOperation>() {
      {
        Sort sort = pageable.getSort();
        if (sort.isEmpty()) {
          sort = Sort.by(Order.desc("createdAt"));
        }
        add(Aggregation.sort(sort));
        add(skip(pageable.getOffset()));
        add(limit(pageable.getPageSize()));
        add(lookup("users", "authorId", "_id", "author"));
        add(project(CommentResponse.class).and("author").arrayElementAt(0).as("author"));
      }
    };
  }

  /**
   * Find a comment by id
   *
   * @param id comment id
   * @return comment
   */
  private Page<CommentResponse> aggregateFlatComments(Criteria criteria, Pageable pageable) {
    Aggregation aggregation =
        newAggregation(
            match(criteria),
            Aggregation.facet(Aggregation.count().as("totalComments"))
                .as(FacetResult.getCountFacetName())
                .and(addAuthorLookupAndPagination(pageable).toArray(new AggregationOperation[0]))
                .as(FacetResult.getDataFacetName()));

    var result =
        mongoTemplate
            .aggregate(aggregation, Comment.class, CommentFacetResult.class)
            .getUniqueMappedResult();

    return new PageImpl<>(result.getDatas(), pageable, result.getCount("totalComments"));
  }

  /**
   * Find a comment by id
   *
   * @param id comment id
   * @return comment
   */
  private Page<CommentGroup> aggregateGroupedComments(Criteria criteria, Pageable pageable) {

    List<AggregationOperation> operations = addAuthorLookupAndPagination(pageable);
    operations.add(
        group("chapterId")
            .first("comicId")
            .as("comicId")
            .first("chapterId")
            .as("chapterId")
            .push("$$ROOT")
            .as("comments"));

    Aggregation aggregation =
        newAggregation(
            match(criteria),
            Aggregation.facet(Aggregation.count().as("totalComments"))
                .as(FacetResult.getCountFacetName())
                .and(operations.toArray(new AggregationOperation[0]))
                .as(FacetResult.getDataFacetName()));

    CommentGroupFacetResult result =
        mongoTemplate
            .aggregate(aggregation, Comment.class, CommentGroupFacetResult.class)
            .getUniqueMappedResult();

    return new PageImpl<>(result.getDatas(), pageable, result.getCount("totalComments"));
  }

  /**
   * Build filter criteria for comments
   *
   * @param comicId comic id
   * @param chapterId chapter id
   * @param parentId parent comment id
   * @return criteria
   */
  private Criteria buildFilterCommentCriteria(String comicId, String chapterId, String parentId) {
    var criteria = Criteria.where("comicId").is(new ObjectId(comicId));

    if (parentId == null) {
      criteria.and("parentId").exists(false);
    } else {
      criteria.and("parentId").is(new ObjectId(parentId));
    }

    if (chapterId == null) {
      criteria.and("chapterId").exists(false);
    } else {
      criteria.and("chapterId").is(new ObjectId(chapterId));
    }

    return criteria;
  }

  class CommentFacetResult extends FacetResult<CommentResponse> {

    public CommentFacetResult(
        List<CommentResponse> dataFacet, List<Map<String, Object>> countFacet) {
      super(dataFacet, countFacet);
    }
  }

  class CommentGroupFacetResult extends FacetResult<CommentGroup> {

    public CommentGroupFacetResult(
        List<CommentGroup> dataFacet, List<Map<String, Object>> countFacet) {
      super(dataFacet, countFacet);
    }
  }
}
