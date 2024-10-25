package com.comic.server.feature.comic.service;

import com.comic.server.feature.comic.dto.ComicDTO;
import com.comic.server.feature.comic.dto.ComicDetailDTO;
import com.comic.server.feature.comic.model.chapter.ShortInfoChapter;
import com.comic.server.feature.comic.model.thirdparty.SourceName;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** ChainComicServiceApi */
public interface ChainGetComicService {

  ChainGetComicService getNextService();

  long countComics();

  ComicDetailDTO getComicDetail(String comicId, SourceName sourceName, Pageable pageable);

  Page<ComicDTO> getComicsWithCategories(Pageable pageable, List<String> filterCategoryIds);

  Page<ComicDTO> searchComic(String keyword, Pageable pageable);

  // CompletableFuture<Page<ComicDTO>> getComicsWithCategories(
  //     Pageable pageable, List<String> filterCategoryIds);

  List<ShortInfoChapter> getChaptersByComicId(String comicId);
}
