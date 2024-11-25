package com.comic.server.feature.user.dto;

import com.comic.server.feature.user.enums.RoleType;
import com.comic.server.feature.user.enums.UserStatus;
import com.comic.server.feature.user.model.User;
import com.comic.server.validation.annotation.OptimizedName;
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
  @Schema(description = "The public id of the account")
  private String pubId;

  @OptimizedName
  @Schema(description = "The name of the account", requiredMode = RequiredMode.REQUIRED)
  private String name;

  @Schema(description = "The avatar path of the account")
  private String avatar;

  @Schema(description = "The status of the account")
  private UserStatus status;

  @Schema(description = "The roles of the account")
  private Set<RoleType> roles;

  private boolean isEnabled;

  public static UserProfile from(User user) {
    return UserProfile.builder()
        .pubId(user.getPubId())
        .name(user.getName())
        .avatar(user.getAvatar())
        .status(user.getStatus())
        .roles(user.getRoles())
        .isEnabled(user.isEnabled())
        .build();
  }

  @Override
  public int hashCode() {
    return pubId.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    else if (obj instanceof User) {
      User that = (User) obj;
      return pubId.equals(that.getPubId());
    }
    return false;
  }
}
