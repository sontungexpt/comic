package com.comic.server.feature.comic.adapter;

import com.comic.server.feature.comic.dto.ComicDTO;
import com.comic.server.feature.comic.dto.ComicDetailDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApiAdapter {

  Page<ComicDTO> search(String keyword, Pageable pageable);

  ComicDetailDTO getComicDetail(String comicId);

  void handleAutoImport();
}
