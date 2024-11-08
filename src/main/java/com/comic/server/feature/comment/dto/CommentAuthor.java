package com.comic.server.feature.comment.dto;

import com.comic.server.feature.user.model.User;
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

  public static CommentAuthor from(User user) {
    return new CommentAuthor(user.getPubId(), user.getName(), user.getAvatar());
  }
}
