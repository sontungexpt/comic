package com.comic.server.feature.user.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "followed_comics")
public class FollowedComic {

  @Id private String id;

  private ObjectId userId;

  @JsonGetter("userId")
  public String getUserId() {
    return userId.toHexString();
  }

  @NotBlank private ObjectId comicId;

  @JsonGetter("comicId")
  public String getComicId() {
    return comicId.toHexString();
  }

  public FollowedComic(ObjectId userId, ObjectId comicId) {
    this.userId = userId;
    this.comicId = comicId;
  }

  @JsonCreator
  public FollowedComic(
      @JsonProperty("userId") String userId, @JsonProperty("comicId") String comicId) {
    this.userId = new ObjectId(userId);
    this.comicId = new ObjectId(comicId);
  }

  @CreatedDate private Instant createdAt;

  @LastModifiedDate private Instant updatedAt;
}
