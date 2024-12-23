package com.comic.server.feature.comic.dto.otruyen;

import com.comic.server.feature.comic.model.ThirdPartySource;
import com.comic.server.feature.comic.model.chapter.ChapterType;
import com.comic.server.feature.comic.model.chapter.ComicChapter;
import com.comic.server.feature.comic.model.chapter.ComicChapter.ImagePage;
import com.comic.server.feature.comic.model.chapter.ComicChapter.RelativeSourceInfo;
import com.comic.server.feature.comic.model.chapter.ShortInfoChapter;
import com.comic.server.feature.comic.model.thirdparty.SourceName;
import java.util.List;
import org.bson.types.ObjectId;

// @Component
public class OtruyenComicChapterAdapter {

  public static ShortInfoChapter convertToShortInfoChapter(
      OtruyenChapterShortInfo chapter, ObjectId comicId) {

    // String chapterApiData = chapter.getChapterApiData();

    return ShortInfoChapter.builder()
        .id(chapter.getId())
        .thumbnailUrl("")
        .comicId(comicId)
        .type(ChapterType.COMIC)
        .thirdPartySource(
            ThirdPartySource.builder()
                .name(SourceName.OTRUYEN)
                .id(chapter.getId())
                .description("Chapter được lấy từ otruyenapi.com")
                .build())
        .name(chapter.getChapterTitle())
        .num(Double.parseDouble(chapter.getChapterName()))
        .build();
  }

  public static ComicChapter convertToComicChapter(
      OtruyenChapterDetail chapter, String comicId, String imageBaseUrl) {

    List<ImagePage> imagePages =
        chapter.getChapterImages().stream()
            .map(page -> new ImagePage(page.getImagePage(), page.getImageFile()))
            .toList();

    return ComicChapter.builder()
        .id(chapter.getId())
        .thumbnailUrl("")
        .comicId(new ObjectId(comicId))
        .type(ChapterType.COMIC)
        .thirdPartySource(
            ThirdPartySource.builder()
                .name(SourceName.OTRUYEN)
                .id(chapter.getId())
                .description("Chapter được lấy từ otruyenapi.com")
                .build())
        .name(chapter.getChapterTitle())
        .num(Double.parseDouble(chapter.getChapterName()))
        .description("")
        .resourceInfo(new RelativeSourceInfo(imageBaseUrl) {})
        .imagePages(imagePages)
        .build();
  }
}
