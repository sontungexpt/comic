package com.comic.server.feature.comic.repository;

import com.comic.server.feature.comic.model.ComicCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComicCategoryRepository extends MongoRepository<ComicCategory, String> {}
