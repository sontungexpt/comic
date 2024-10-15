package com.comic.server.feature.comic.dto.otruyen;

import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.List;
import lombok.Data;

@Data
public class OtruyenComic {

  @JsonSetter("_id")
  private String id;

  @JsonSetter("name")
  private String name;

  @JsonSetter("slug")
  private String slug;

  @JsonSetter("origin_name")
  private List<String> originName;

  @JsonSetter("content")
  private String content;

  @JsonSetter("status")
  private String status;

  @JsonSetter("thumb_url")
  private String thumbUrl;

  @JsonSetter("sub_docquyen")
  private boolean subDocQuyen;

  @JsonSetter("author")
  private List<String> author;

  @JsonSetter("category")
  private List<OtruyenCategory> category;

  @JsonSetter("chapters")
  private List<OtruyenServerData> serverDatas;
}
