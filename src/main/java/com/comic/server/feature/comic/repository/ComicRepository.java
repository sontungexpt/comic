package com.comic.server.feature.comic.repository;

import com.comic.server.feature.comic.model.Comic;
import com.comic.server.feature.comic.model.thirdparty.SourceName;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComicRepository extends MongoRepository<Comic, String> {

  Optional<Comic> findBySlug(String slug);

  Optional<Comic> findByName(String name);

  Optional<Comic> findByIdAndThirdPartySourceName(String id, SourceName sourceName);

  List<Comic> findByThirdPartySourceName(SourceName sourceName);

  boolean existsByIdAndThirdPartySourceName(String id, SourceName sourceName);
}
