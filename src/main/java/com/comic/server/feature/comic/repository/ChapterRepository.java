package com.comic.server.feature.comic.repository;

import com.comic.server.feature.comic.model.chapter.Chapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterRepository extends MongoRepository<Chapter, String> {

  Page<Chapter> findByComicId(String comicId, Pageable pageable);
}
