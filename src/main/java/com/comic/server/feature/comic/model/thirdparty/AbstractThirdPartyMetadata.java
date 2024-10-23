package com.comic.server.feature.comic.model.thirdparty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "third_party_metadata")
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "sourceName")
public abstract class AbstractThirdPartyMetadata implements Serializable, Persistable<String> {

  @Id private String id;

  private SourceName sourceName;

  @CreatedDate protected Instant createdAt;

  @LastModifiedDate protected Instant updatedAt;

  public AbstractThirdPartyMetadata(SourceName sourceName) {
    this.sourceName = sourceName;
  }

  @Override
  @JsonIgnore
  public boolean isNew() {
    return id == null || createdAt == null;
  }
}
