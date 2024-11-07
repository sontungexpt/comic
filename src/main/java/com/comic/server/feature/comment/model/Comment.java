package com.comic.server.feature.comment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

@Getter
@Setter
@Document(collection = "comments")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment implements Serializable {

  @Schema(hidden = true)
  @Id
  private String id;

  @Schema(description = "The parent comment id", example = "60f3b3b3e4b0f3b3b3e4b0f3")
  @NotNull
  private ObjectId parentId;

  public boolean isRoot() {
    return parentId == null;
  }

  public void replyTo(@NonNull Comment parent) {
    this.parentId = new ObjectId(parent.getId());
    this.treePath = parent.getTreePath() + parent.getId() + "/";
    this.depth = parent.getDepth() + 1;
  }

  @Schema(hidden = true)
  @Default
  private int depth = 1;

  @Schema(hidden = true)
  @JsonIgnore
  @Default
  private String treePath = "/";

  public final List<String> getParentIds() {
    return List.of(treePath.substring(1, treePath.length() - 1).split("/"));
  }

  @Schema(hidden = true)
  private ObjectId comicId;

  @Default
  @Setter(AccessLevel.NONE)
  private int totalReplies = 0;

  public void increaseTotalReplies(int count) {
    this.totalReplies += count;
  }

  public void decreaseTotalReplies(int count) {
    this.totalReplies -= count;
  }

  @Schema(hidden = true)
  private ObjectId chapterId;

  @Schema(hidden = true)
  @CreatedBy
  @JsonIgnore
  private ObjectId authorId;

  // This is a transient field, it will not be persisted in the database.
  // It is used to store the author name of the comment and easily convert it to a CommentResponse
  @Transient private String authorName;

  // This is a transient field, it will not be persisted in the database.
  // It is used to store the author avatar of the comment and easily convert it to a CommentResponse
  @Transient private String authorAvatar;

  @Schema(description = "The content of the comment", example = "This is a comment")
  @NotBlank
  private String content;

  @CreatedDate private Instant createdAt;

  @LastModifiedDate private Instant updatedAt;
}
