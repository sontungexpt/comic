package com.comic.server.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;
import org.bson.types.ObjectId;

public interface JsonIgnoreAuditable {
  @JsonIgnore
  ObjectId getCreatedBy();

  @JsonIgnore
  Instant getCreatedAt();

  @JsonIgnore
  String getUpdatedBy();

  @JsonIgnore
  Instant getUpdatedAt();
}
