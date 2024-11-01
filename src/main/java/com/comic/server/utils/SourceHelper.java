package com.comic.server.utils;

import com.comic.server.feature.comic.model.OriginalSource;
import com.comic.server.feature.comic.model.ThirdPartySource;

public class SourceHelper {

  public static OriginalSource resolveOriginalSource(
      OriginalSource originalSource, ThirdPartySource thirdPartySource) {
    if (originalSource == null && thirdPartySource != null) {
      return OriginalSource.builder()
          .name(thirdPartySource.getName())
          .description(thirdPartySource.getDescription())
          .link(thirdPartySource.getLink())
          .build();
    }
    return originalSource;
  }
}
