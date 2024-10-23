package com.comic.server.feature.comic.dto.otruyen;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtruyenComic implements Serializable {

  @JsonProperty("_id")
  private String id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("slug")
  private String slug;

  @JsonProperty("origin_name")
  private List<String> originName;

  @JsonProperty("content")
  private String content;

  @JsonProperty("status")
  private String status;

  @JsonProperty("thumb_url")
  private String thumbUrl;

  @JsonProperty("sub_docquyen")
  private boolean subDocQuyen;

  @JsonProperty("author")
  private List<String> author;

  @JsonProperty("category")
  private List<OtruyenCategory> category;

  @JsonProperty("chapters")
  private List<OtruyenServerData> serverDatas;

  @JsonProperty("chaptersLatest")
  private List<OtruyenChapterShortInfo> chaptersLatest;
}
