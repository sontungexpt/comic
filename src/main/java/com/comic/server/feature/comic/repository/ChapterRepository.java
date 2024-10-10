package com.comic.server.feature.comic.repository;

import com.comic.server.feature.comic.model.Chapter;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterRepository extends MongoRepository<Chapter, String> {

  List<Chapter> findByComicId(String comicId);
}
