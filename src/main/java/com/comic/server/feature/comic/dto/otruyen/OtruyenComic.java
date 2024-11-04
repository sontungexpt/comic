package com.comic.server.feature.comic.dto.otruyen;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIncludeProperties({
  "_id",
  "name",
  "slug",
  "origin_name",
  "content",
  "status",
  "thumb_url",
  "sub_docquyen",
  "author",
  "category",
  "chapters",
  "chaptersLatest"
})
public class OtruyenComic implements Serializable {

  public static final String CDN_IMAGE_URL = "https://img.otruyenapi.com/uploads/comics";

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

  private String thumbUrl;

  @JsonSetter("thumb_url")
  public void setThumbUrl(String thumbUrl) {
    this.thumbUrl = CDN_IMAGE_URL + "/" + thumbUrl;
  }

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
