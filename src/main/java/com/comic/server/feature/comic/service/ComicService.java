package com.comic.server.feature.comic.service;

import com.comic.server.feature.comic.model.Comic;
import java.util.List;

public interface ComicService extends ChainGetComicService {

  Comic createComic(Comic comic);

  List<Comic> createComics(Iterable<Comic> comics);

  boolean existsById(String id);

  Comic getComicById(String comicId);
}
