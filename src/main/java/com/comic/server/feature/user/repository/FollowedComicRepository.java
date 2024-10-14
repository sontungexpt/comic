package com.comic.server.feature.user.repository;

import com.comic.server.feature.user.model.FollowedComic;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowedComicRepository extends MongoRepository<FollowedComic, String> {
  List<FollowedComic> findByUserId(String userId);

  List<FollowedComic> findByComicId(String comicId);

  FollowedComic findByUserIdAndComicId(String userId, String comicId);

  void deleteByUserIdAndComicId(String userId, String comicId);
}
