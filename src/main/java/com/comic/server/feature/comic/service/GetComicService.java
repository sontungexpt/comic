package com.comic.server.feature.comic.service;

import com.comic.server.feature.comic.dto.ComicDTO;
import com.comic.server.feature.comic.dto.ComicDetailDTO;
import com.comic.server.feature.comic.model.chapter.ShortInfoChapter;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetComicService {

  ComicDetailDTO getComicDetail(String comicId, Pageable pageable);

  Page<ComicDTO> getComicsWithCategories(Pageable pageable);

  List<ShortInfoChapter> getChaptersByComicId(String comicId);
}
