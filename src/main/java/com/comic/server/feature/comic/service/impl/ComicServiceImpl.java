package com.comic.server.feature.comic.service.impl;

import com.comic.server.exceptions.ResourceNotFoundException;
import com.comic.server.feature.comic.dto.ComicDTO;
import com.comic.server.feature.comic.model.Comic;
import com.comic.server.feature.comic.repository.ComicRepository;
import com.comic.server.feature.comic.repository.CustomComicRepository;
import com.comic.server.feature.comic.service.ComicService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public record ComicServiceImpl(
    MongoTemplate mongoTemplate,
    ComicRepository comicRepository,
    CustomComicRepository customComicRepository)
    implements ComicService {

  @Override
  public Comic createComic(Comic comic) {
    return comicRepository.save(comic);
  }

  @Override
  public List<Comic> createComics(Iterable<Comic> comics) {
    return comicRepository.saveAll(comics);
  }

  @Override
  public Comic getComic(String comicId) {
    return comicRepository
        .findById(comicId)
        .orElseThrow(() -> new ResourceNotFoundException(Comic.class, "id", comicId));
  }

  @Override
  public long countComics() {
    return comicRepository.count();
  }

  @Override
  public Page<ComicDTO> getComicsWithCategories(Pageable pageable) {
    return customComicRepository.findAllWithCategories(pageable);
  }
}
