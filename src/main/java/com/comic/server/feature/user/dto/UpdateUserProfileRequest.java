package com.comic.server.feature.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public class UpdateUserProfileRequest {

  @Schema(description = "The name of the account", requiredMode = RequiredMode.REQUIRED)
  private String name;

  @Schema(description = "The avatar path of the account")
  private String avatar;
}
