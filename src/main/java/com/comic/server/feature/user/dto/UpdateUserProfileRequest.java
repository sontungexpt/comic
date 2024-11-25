package com.comic.server.feature.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserProfileRequest implements Serializable {

  @Schema(
      description = "The name of the account",
      requiredMode = RequiredMode.NOT_REQUIRED,
      example = "New name")
  private String name;

  @Schema(description = "The avatar path of the account", requiredMode = RequiredMode.NOT_REQUIRED)
  private String avatar;
}
