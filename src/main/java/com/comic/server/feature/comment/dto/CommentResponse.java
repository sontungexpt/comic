package com.comic.server.feature.comment.dto;

import com.comic.server.feature.comment.model.Comment;
import com.comic.server.feature.user.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentResponse {

  private String id;

  private String content;

  private String comicId;

  private String chapterId;

  private Instant updatedAt;

  private long totalReplies;

  private CommentAuthor author;

  // This feild is used to store the replies of a comment
  private List<CommentResponse> replies;

  private String parentId;

  public static CommentResponse from(Comment comment, User user) {
    return CommentResponse.builder()
        .id(comment.getId())
        .content(comment.getContent())
        .author(CommentAuthor.from(user))
        .comicId(comment.getComicId().toHexString())
        .chapterId(comment.getChapterId().toHexString())
        .updatedAt(comment.getUpdatedAt())
        .totalReplies(comment.getTotalReplies())
        .replies(List.of())
        .parentId(comment.getParentId() == null ? null : comment.getParentId().toHexString())
        .build();
  }
}
