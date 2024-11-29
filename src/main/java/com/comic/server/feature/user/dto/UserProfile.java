package com.comic.server.feature.user.dto;

import com.comic.server.feature.user.enums.RoleType;
import com.comic.server.feature.user.enums.UserStatus;
import com.comic.server.feature.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@Builder
public class UserProfile {
  @Schema(description = "The name of the account", requiredMode = RequiredMode.REQUIRED)
  private String name;

  @Schema(description = "The avatar path of the account")
  private String avatar;

  @Schema(description = "The number of comics created by user")
  private int totalCreatedComics;

  @Schema(description = "The status of the account")
  private UserStatus status;

  @Schema(description = "The roles of the account")
  private Set<RoleType> roles;

  private boolean isEnabled;

  public static UserProfile from(User user) {
    return UserProfile.builder()
        .name(user.getName())
        .avatar(user.getAvatar())
        .status(user.getStatus())
        .roles(user.getRoles())
        .isEnabled(user.isEnabled())
        .build();
  }
}
