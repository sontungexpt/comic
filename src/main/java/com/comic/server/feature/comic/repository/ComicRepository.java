package com.comic.server.feature.comic.repository;

import com.comic.server.feature.comic.model.Comic;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComicRepository extends MongoRepository<Comic, String> {

  Optional<Comic> findBySlug(String slug);
}
