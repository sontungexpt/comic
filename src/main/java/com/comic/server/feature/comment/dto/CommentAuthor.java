package com.comic.server.feature.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommentAuthor {

  private String pubId;

  private String name;

  private String avatar;

  public String getAvatar() {
    return avatar != null ? avatar : "";
  }
}
