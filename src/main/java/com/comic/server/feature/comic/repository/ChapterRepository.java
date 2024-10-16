package com.comic.server.feature.comic.repository;

import com.comic.server.feature.comic.model.chapter.AbstractChapter;
import com.comic.server.feature.comic.model.chapter.ShortInfoChapter;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterRepository extends MongoRepository<AbstractChapter, String> {

  Page<AbstractChapter> findByComicId(String comicId, Pageable pageable);

  List<ShortInfoChapter> findByComicIdOrderByNumDesc(ObjectId comicId);

  Page<ShortInfoChapter> findByComicIdOrderByNumDesc(ObjectId comicId, Pageable pageable);
}
