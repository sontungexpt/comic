package com.comic.server.feature.comic.service;

import com.comic.server.feature.comic.model.Comic;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ComicService {

  Comic createComic(Comic comic);

  List<Comic> createComics(Iterable<Comic> comics);

  Comic getComic(String comicId);

  Page<Comic> getComics(Pageable pageable);

  long countComics();
}
