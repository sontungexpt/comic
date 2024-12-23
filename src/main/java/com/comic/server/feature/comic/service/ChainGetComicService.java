package com.comic.server.feature.comic.service;

import com.comic.server.feature.comic.dto.ComicDTO;
import com.comic.server.feature.comic.dto.ComicDetailDTO;
import com.comic.server.feature.comic.model.chapter.ShortInfoChapter;
import com.comic.server.feature.comic.model.thirdparty.SourceName;
import com.comic.server.feature.user.model.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChainGetComicService {

  ChainGetComicService getNextService();

  long countComics(List<String> filterCategoryIds);

  ComicDetailDTO getComicDetail(
      String comicId, SourceName sourceName, Pageable pageable, User user);

  Page<ComicDTO> getComicsWithCategories(Pageable pageable, List<String> filterCategoryIds);

  Page<ComicDTO> searchComic(String keyword, Pageable pageable);

  List<ShortInfoChapter> getChaptersByComicId(String comicId);

  ShortInfoChapter getFirstChapterByComicId(String comicId);
}
