package com.comic.server.feature.comic.dto.otruyen;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class OtruyenServerData {

  @JsonProperty("server_name")
  private String serverName;

  @JsonProperty("server_data")
  private List<OtruyenChapterShortInfo> chapters;
}
