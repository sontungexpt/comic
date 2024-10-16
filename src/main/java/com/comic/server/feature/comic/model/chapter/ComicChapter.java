package com.comic.server.feature.comic.model.chapter;

import com.comic.server.feature.comic.model.chapter.ComicChapter.ResourceInfo.PathType;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@JsonTypeName(ChapterType.Fields.COMIC)
public class ComicChapter extends AbstractChapter {

  @Getter
  @JsonTypeInfo(
      use = JsonTypeInfo.Id.NAME,
      visible = true,
      include = JsonTypeInfo.As.PROPERTY,
      defaultImpl = AbsoluteSourceInfo.class,
      property = "type")
  public abstract static class ResourceInfo {
    @FieldNameConstants
    public enum PathType {
      @FieldNameConstants.Include
      ABSOLUTE,

      @FieldNameConstants.Include
      RELATIVE
    }

    protected PathType type;
  }

  @JsonTypeName(PathType.Fields.ABSOLUTE)
  public static class AbsoluteSourceInfo extends ResourceInfo {
    public AbsoluteSourceInfo() {
      this.type = PathType.ABSOLUTE;
    }
  }

  @Getter
  @Setter
  @JsonTypeName(PathType.Fields.RELATIVE)
  public static class RelativeSourceInfo extends ResourceInfo {
    private String baseUrl;

    public RelativeSourceInfo(String baseUrl) {
      this.type = PathType.RELATIVE;
      this.baseUrl = baseUrl;
    }
  }

  // @NotNull private ResourceInfo sourceInfo;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Page {

    private int number;

    private String path;

    public String getImageFileName() {
      return "chapter-" + number + ".jpg";
    }
  }

  public ComicChapter() {
    super();
    setType(ChapterType.COMIC);
  }

  public ComicChapter(AbstractChapter chapter) {
    super(chapter);
    setType(ChapterType.COMIC);
  }

  // public StoredFile getUpload() {
  //   StoredFile file = new StoredFile();
  //   // pages.forEach(page -> file.setPreloadedFile(page.getPath()));
  //   return file;
  // }

  // public String getUploadPath() {
  //   return "comic/" + getId();
  // }

  // @NotEmpty List<Page> pages;
}
