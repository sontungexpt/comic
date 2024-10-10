package com.comic.server.feature.user.model;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "followed_comics")
public class FollowedComic {

  @Id private String id;

  @CreatedBy private String userId;

  @NotBlank private String comicId;

  @CreatedDate private Instant createdAt;

  @LastModifiedDate private Instant updatedAt;
}
