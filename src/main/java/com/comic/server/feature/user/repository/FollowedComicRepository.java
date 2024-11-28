package com.comic.server.feature.user.repository;

import com.comic.server.feature.user.model.FollowedComic;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowedComicRepository extends MongoRepository<FollowedComic, String> {
  List<FollowedComic> findByUserId(ObjectId userId);

  List<FollowedComic> findByComicId(ObjectId comicId);

  FollowedComic findByUserIdAndComicId(ObjectId userId, ObjectId comicId);

  int deleteByUserIdAndComicId(ObjectId userId, ObjectId comicId);

  boolean existsByUserIdAndComicId(ObjectId userId, ObjectId comicId);
}
