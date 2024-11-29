package com.comic.server.feature.comic.dto.otruyen;

import com.comic.server.feature.comic.dto.ComicDTO;
import com.comic.server.feature.comic.dto.ComicDetailDTO;
import com.comic.server.feature.comic.model.Author;
import com.comic.server.feature.comic.model.Comic;
import com.comic.server.feature.comic.model.ComicCategory;
import com.comic.server.feature.comic.model.ThirdPartySource;
import com.comic.server.feature.comic.model.chapter.ShortInfoChapter;
import com.comic.server.feature.comic.model.thirdparty.SourceName;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OtruyenComicAdapter {

  private final OtruyenComicCategoryAdapter otruyenComicCategoryAdapter;

  private final String DESCRIPTION = "Truyện được lấy từ otruyenapi.com";

  @Getter
  @Setter
  public class ComicWithCategories {
    private Comic comic;
    private List<ComicCategory> categories;

    public ComicWithCategories(Comic comic, List<ComicCategory> categories) {
      this.comic = comic;
      this.categories = categories;
    }
  }

  public ComicWithCategories convertToComic(OtruyenComic comic, boolean includedId) {
    List<ComicCategory> categories =
        comic.getCategory().stream()
            .map(otruyenComicCategoryAdapter::convertToComicCategory)
            .toList();

    ObjectId comic_id = new ObjectId();
    var result =
        new ComicWithCategories(
            Comic.builder()
                .id(includedId ? comic_id.toHexString() : null)
                .name(comic.getName())
                .authors(
                    comic.getAuthor() != null
                        ? comic.getAuthor().stream().map(name -> new Author(name)).toList()
                        : null)
                .categoryIds(
                    categories.stream().map(category -> new ObjectId(category.getId())).toList())
                .artists(List.of())
                .translators(List.of())
                .tags(List.of())
                .characters(List.of())
                .newChapters(
                    comic.getChaptersLatest() != null
                        ? comic.getChaptersLatest().stream()
                            .map(
                                chapter ->
                                    OtruyenComicChapterAdapter.convertToShortInfoChapter(
                                        chapter, includedId ? comic_id : null))
                            .toList()
                        : List.of())
                .status(OtruyenComicStatusAdapter.convertToStatus(comic.getStatus()))
                .alternativeNames(comic.getOriginName())
                .summary(comic.getContent())
                .thumbnailUrl(comic.getThumbUrl())
                .thirdPartySource(
                    ThirdPartySource.builder()
                        .name(SourceName.OTRUYEN)
                        .id(comic.getId())
                        .slug(comic.getSlug())
                        .description(DESCRIPTION)
                        .build())
                .build(),
            categories);

    return result;
  }

  public ComicDTO convertToComicDTO(OtruyenComic comic) {

    return ComicDTO.builder()
        .name(comic.getName())
        .authors(comic.getAuthor().stream().map(Author::new).toList())
        .artists(List.of())
        .translators(List.of())
        .categories(
            comic.getCategory().stream()
                .map(category -> otruyenComicCategoryAdapter.convertToComicCategory(category))
                .toList())
        .tags(List.of())
        .characters(List.of())
        .newChapters(
            comic.getChaptersLatest().stream()
                .map(chapter -> OtruyenComicChapterAdapter.convertToShortInfoChapter(chapter, null))
                .toList())
        .status(OtruyenComicStatusAdapter.convertToStatus(comic.getStatus()))
        .alternativeNames(comic.getOriginName())
        .summary(comic.getContent())
        .thumbnailUrl(comic.getThumbUrl())
        .newChapterUpdatedAt(Instant.now())
        .thirdPartySource(
            ThirdPartySource.builder()
                .name(SourceName.OTRUYEN)
                .id(comic.getId())
                .slug(comic.getSlug())
                .description(DESCRIPTION)
                .build())
        .build();
  }

  public ComicDetailDTO convertToComicDetailDTO(
      OtruyenComic comic, String realComicId, Pageable pageable) {

    var serverData = comic.getServerDatas();
    List<ShortInfoChapter> chapters =
        serverData.isEmpty()
            ? List.of()
            : serverData.get(0).getChapters().stream()
                .map(
                    chapter ->
                        OtruyenComicChapterAdapter.convertToShortInfoChapter(
                            chapter, new ObjectId(realComicId)))
                .toList();

    long totalElements = chapters.size();

    List<ShortInfoChapter> newChapters =
        comic.getChaptersLatest() != null
            ? comic.getChaptersLatest().stream()
                .map(
                    chapter ->
                        OtruyenComicChapterAdapter.convertToShortInfoChapter(
                            chapter, new ObjectId(realComicId)))
                .toList()
            : (totalElements > 3
                ? chapters.stream().skip(totalElements - 3).limit(3).toList()
                : chapters);

    return ComicDetailDTO.builder()
        .id(realComicId)
        .name(comic.getName())
        .authors(
            comic.getAuthor() != null ? comic.getAuthor().stream().map(Author::new).toList() : null)
        .characters(List.of())
        .artists(List.of())
        .slug(comic.getSlug())
        .categories(
            comic.getCategory().stream()
                .map(category -> otruyenComicCategoryAdapter.convertToComicCategory(category))
                .toList())
        .tags(List.of())
        .characters(List.of())
        .translators(List.of())
        .newChapters(newChapters)
        .status(OtruyenComicStatusAdapter.convertToStatus(comic.getStatus()))
        .alternativeNames(comic.getOriginName())
        .summary(comic.getContent())
        .thumbnailUrl(comic.getThumbUrl())
        .newChapterUpdatedAt(Instant.now())
        .chapters(
            new PageImpl<>(
                chapters.stream().skip(pageable.getOffset()).limit(pageable.getPageSize()).toList(),
                pageable,
                chapters.size()))
        .thirdPartySource(
            ThirdPartySource.builder()
                .name(SourceName.OTRUYEN)
                .id(comic.getId())
                .slug(comic.getSlug())
                .description(DESCRIPTION)
                .build())
        .build();
  }
}
