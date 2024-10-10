package com.comic.server.feature.comic.repository;

import com.comic.server.feature.comic.model.ComicCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComicCategoryRepository extends MongoRepository<ComicCategory, String> {

  List<ComicCategory> findByDeleted(boolean deleted);

  Page<ComicCategory> findByDeleted(boolean deleted, Pageable pageable);

  Optional<ComicCategory> findByName(String name);
}
