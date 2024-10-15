package com.comic.server.feature.comic.dto.otruyen;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OtruyenParams {
  @JsonProperty("slug")
  private String slug;

  @JsonProperty("crawl_check_url")
  private String crawlCheckUrl;
}
