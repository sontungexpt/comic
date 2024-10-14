package com.comic.server.feature.comic.model.chapter;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@JsonTypeName(ChapterType.Fields.COMIC)
public class ComicChapter extends Chapter {

  @Getter
  public abstract static class ResourceInfo {
    public enum PathType {
      ABSOLUTE,
      RELATIVE
    }

    protected PathType type;
  }

  public static class AbsoluteSourceInfo extends ResourceInfo {
    public AbsoluteSourceInfo() {
      this.type = PathType.ABSOLUTE;
    }
  }

  @Getter
  @Setter
  public static class RelativeSourceInfo extends ResourceInfo {
    private String baseUrl;

    public RelativeSourceInfo(String baseUrl) {
      this.type = PathType.RELATIVE;
      this.baseUrl = baseUrl;
    }
  }

  @NotNull private ResourceInfo sourceInfo;

  @Data
  @AllArgsConstructor
  public static class Page {

    private int number;

    private String path;

    public String getImageFileName() {
      return "chapter-" + number + ".jpg";
    }
  }

  @NotEmpty List<Page> pages;
}
