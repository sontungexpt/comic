package com.comic.server.feature.comic.repository;

import com.comic.server.feature.comic.dto.ComicDTO;
import com.comic.server.feature.comic.dto.ComicDetailDTO;
import com.comic.server.feature.comic.model.Comic;
import com.comic.server.feature.user.repository.FollowedComicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomComicRepository {
  private final MongoTemplate mongoTemplate;
  private final ChapterRepository chapterRepository;
  private final FollowedComicRepository followedComicRepository;

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

  public ComicDetailDTO findComicDetail(String comicId, Pageable pageable) {
    var chapters = chapterRepository.findByComicId(comicId, pageable);

    Aggregation aggregation =
        Aggregation.newAggregation(
            Aggregation.match(Criteria.where("_id").is(comicId)),
            Aggregation.lookup("comic_categories", "categoryIds", "_id", "categories"));

    var comics =
        mongoTemplate
            .aggregate(aggregation, Comic.class, ComicDetailDTO.class)
            .getUniqueMappedResult();

    // comics.setFollowed(followedComicRepository.existsByUserIdAndComicId(userId, comicId));
    comics.setChapters(chapters);
    return comics;
  }
}
