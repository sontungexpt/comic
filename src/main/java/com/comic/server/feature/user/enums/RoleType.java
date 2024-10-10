package com.comic.server.feature.user.enums;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants
public enum RoleType {
  @FieldNameConstants.Include
  ADMIN,
  @FieldNameConstants.Include
  READER,
  @FieldNameConstants.Include
  POSTER,
}
