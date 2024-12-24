package com.comic.server.feature.history.repository;

import com.comic.server.feature.history.model.ReadHistory;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadHistoryRepository extends MongoRepository<ReadHistory, String> {

  Optional<ReadHistory> findByUserIdAndComicId(ObjectId userId, ObjectId comicId);

  void deleteByUserId(ObjectId objectId);

  Optional<ReadHistory> findByUserId(ObjectId objectId);

  void deleteByUserIdAndComicId(ObjectId objectId, ObjectId objectId2);
}
