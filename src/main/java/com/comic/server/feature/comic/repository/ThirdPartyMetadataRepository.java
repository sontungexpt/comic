package com.comic.server.feature.comic.repository;

import com.comic.server.feature.comic.model.thirdparty.AbstractThirdPartyMetadata;
import com.comic.server.feature.comic.model.thirdparty.SourceName;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdPartyMetadataRepository
    extends MongoRepository<AbstractThirdPartyMetadata, String> {

  Optional<AbstractThirdPartyMetadata> findBySourceName(SourceName sourceName);
}
