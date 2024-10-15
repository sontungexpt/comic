package com.comic.server.feature.comic.service.impl;

import com.comic.server.feature.comic.dto.ComicDTO;
import com.comic.server.feature.comic.dto.ComicDetailDTO;
import com.comic.server.feature.comic.model.Comic;
import com.comic.server.feature.comic.model.chapter.ShortInfoChapter;
import com.comic.server.feature.comic.repository.ChapterRepository;
import com.comic.server.feature.comic.repository.ComicRepository;
import com.comic.server.feature.comic.repository.CustomComicRepository;
import com.comic.server.feature.comic.service.ChainGetComicService;
import com.comic.server.feature.comic.service.ComicService;
import com.comic.server.feature.comic.service.GetComicService;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public record ComicServiceImpl(
    MongoTemplate mongoTemplate,
    ComicRepository comicRepository,
    ChapterRepository chapterRepository,
    CustomComicRepository customComicRepository,
    OtruyenComicServiceImpl otruyenComicService)
    implements ComicService, ChainGetComicService {

  @Override
  public Comic createComic(Comic comic) {
    return comicRepository.save(comic);
  }

  @Override
  public List<Comic> createComics(Iterable<Comic> comics) {
    return comicRepository.saveAll(comics);
  }

  @Override
  public long countComics() {
    return comicRepository.count();
  }

  @Override
  public Page<ComicDTO> getComicsWithCategories(Pageable pageable) {
    return customComicRepository.findAllWithCategories(pageable);
  }

  @Override
  public ComicDetailDTO getComicDetail(String comicId, Pageable pageable) {
    ComicDetailDTO comicDetail = customComicRepository.findComicDetail(comicId, pageable);

    // getNextService().getComicDetail(comicDetail.getOriginalSource().getSlugFromSource(),
    // pageable);

    return comicDetail;
  }

  @Override
  public List<ShortInfoChapter> getChaptersByComicId(String comicId) {
    List<ShortInfoChapter> chapters =
        chapterRepository.findByComicIdOrderByNumDesc(new ObjectId(comicId));

    // ConsoleUtils.prettyPrint(
    //     getNextService()
    //         .getChaptersByComicId(
    //             getComicDetail(comicId, Pageable.unpaged())
    //                 .getOriginalSource()
    //                 .getSlugFromSource()));

    return chapters;

    // ConsoleUtils.prettyPrint(object);
  }

  @Override
  public GetComicService getNextService() {
    return otruyenComicService;
  }
}
