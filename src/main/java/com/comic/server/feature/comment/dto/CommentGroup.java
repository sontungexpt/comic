package com.comic.server.feature.comment.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommentGroup {

  private String id;

  private String parentId;

  private String chapterId;

  private List<CommentResponse> comments;

  public static String getCommentListName() {
    return "comments";
  }
}
