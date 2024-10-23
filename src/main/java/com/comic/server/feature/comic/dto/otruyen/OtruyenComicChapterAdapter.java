package com.comic.server.feature.comic.dto.otruyen;

import com.comic.server.feature.comic.model.Source;
import com.comic.server.feature.comic.model.chapter.ChapterType;
import com.comic.server.feature.comic.model.chapter.ShortInfoChapter;
import com.comic.server.feature.comic.model.thirdparty.SourceName;
import org.bson.types.ObjectId;

// @Component
public class OtruyenComicChapterAdapter {

  public static ShortInfoChapter convertToShortInfoChapter(
      OtruyenChapterShortInfo chapter, ObjectId comicId) {

    String chapterApiData = chapter.getChapterApiData();
    String id = chapterApiData.substring(chapterApiData.lastIndexOf("/") + 1);

    return ShortInfoChapter.builder()
        .id(id)
        .thumbnailUrl("")
        .comicId(comicId)
        .type(ChapterType.COMIC)
        .originalSource(
            Source.builder()
                .name(SourceName.OTRUYEN)
                .id(id)
                .description("Chapter được lấy từ otruyenapi.com")
                .build())
        .name(chapter.getChapterTitle())
        .num(Double.parseDouble(chapter.getChapterName()))
        .build();
  }
}
