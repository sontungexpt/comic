package com.comic.server.feature.comic.service.impl;

import com.comic.server.exceptions.ResourceNotFoundException;
import com.comic.server.feature.comic.dto.ComicDTO;
import com.comic.server.feature.comic.dto.ComicDetailDTO;
import com.comic.server.feature.comic.model.Comic;
import com.comic.server.feature.comic.model.chapter.ShortInfoChapter;
import com.comic.server.feature.comic.model.thirdparty.SourceName;
import com.comic.server.feature.comic.repository.ChapterRepository;
import com.comic.server.feature.comic.repository.ComicDetailRepository;
import com.comic.server.feature.comic.repository.ComicRepository;
import com.comic.server.feature.comic.service.ChainGetComicService;
import com.comic.server.feature.comic.service.ComicService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComicServiceImpl implements ComicService {

  private final ComicRepository comicRepository;
  private final ChapterRepository chapterRepository;
  private final ComicDetailRepository comicDetailRepository;
  private final OtruyenComicServiceImpl otruyenComicService;

  @Override
  public Comic createComic(Comic comic) {
    return comicRepository.save(comic);
  }

  @Override
  @CacheEvict(value = "comics", allEntries = true)
  public List<Comic> createComics(Iterable<Comic> comics) {
    return comicRepository.saveAll(comics);
  }

  @Override
  public long countComics() {
    return comicRepository.count();
  }

  @Override
  @Cacheable(value = "comic", key = "'comicId:' + #comicId")
  public ComicDetailDTO getComicDetail(String comicId, SourceName sourceName, Pageable pageable) {
    if (sourceName == SourceName.ROOT) {
      return comicDetailRepository.findComicDetail(comicId, pageable);
    } else {
      return getNextService().getComicDetail(comicId, SourceName.OTRUYEN, pageable);
    }
  }

  @Override
  @Cacheable(value = "comic_chapters", key = "'comicId:' + #comicId")
  public List<ShortInfoChapter> getChaptersByComicId(String comicId) {
    if (comicRepository.existsByIdAndOriginalSourceName(comicId, SourceName.ROOT)) {
      log.info("Fetching chapters from ROOT source");
      return chapterRepository.findByComicIdOrderByNumDesc(new ObjectId(comicId));
    }
    return getNextService().getChaptersByComicId(comicId);
  }

  @Override
  public ChainGetComicService getNextService() {
    return otruyenComicService;
  }

  @Cacheable(
      value = "comics",
      key =
          "'page:' + #pageable.pageNumber + 'size:' + #pageable.pageSize + 'sort:' + #pageable.sort"
              + " + 'filter:' + #filterCategoryIds")
  @Override
  public Page<ComicDTO> getComicsWithCategories(Pageable pageable, List<String> filterCategoryIds) {

    Page<ComicDTO> comics =
        comicDetailRepository.findAllWithCategories(pageable, filterCategoryIds);

    int pageSize = pageable.getPageSize();
    int currentNumberOfElements = comics.getNumberOfElements();
    if (currentNumberOfElements < pageSize) {
      log.info("Fetching more comics from next service");

      int remainingSize = pageSize - currentNumberOfElements;

      Page<ComicDTO> nextComics =
          getNextService()
              .getComicsWithCategories(
                  PageRequest.of(0, remainingSize, pageable.getSort()), filterCategoryIds);

      var returnComics = new ArrayList<>(comics.getContent());

      returnComics.addAll(nextComics.getContent());

      return new PageImpl<>(
          returnComics, pageable, comics.getTotalElements() + nextComics.getTotalElements());
    }

    return new PageImpl<>(
        comics.getContent(), pageable, comics.getTotalElements() + getNextService().countComics());
  }

  @Override
  @Cacheable(
      value = "comics",
      key =
          "'page:' + #pageable.pageNumber + 'size:' + #pageable.pageSize + 'sort:' +"
              + " #pageable.sort"
              + " + 'keyword:' + #keyword")
  public Page<ComicDTO> searchComic(String keyword, Pageable pageable) {

    Page<ComicDTO> comics = comicDetailRepository.findComicDetailByKeyword(keyword, pageable);

    int pageSize = pageable.getPageSize();
    int currentNumberOfElements = comics.getNumberOfElements();

    if (comics.isLast()) {
      int remainingSize = pageSize - currentNumberOfElements;
      Page<ComicDTO> nextComics =
          getNextService()
              .searchComic(keyword, PageRequest.of(0, remainingSize, pageable.getSort()));
      var returnComics = new HashSet<>(comics.getContent());

      returnComics.addAll(nextComics.getContent());

      return new PageImpl<>(
          new ArrayList<>(returnComics),
          pageable,
          comics.getTotalElements() + nextComics.getTotalElements());
    }
    return comics;
  }

  @Override
  public boolean existsById(String id) {
    return comicRepository.existsById(id);
  }

  @Override
  public Comic getComicById(String comicId) {
    return comicRepository
        .findById(comicId)
        .orElseThrow(() -> new ResourceNotFoundException(Comic.class, "id", comicId));
  }

  // @Override
  // public CompletableFuture<Page<ComicDTO>> getComicsWithCategories(
  //     Pageable pageable, List<String> filterCategoryIds) {

  //   return CompletableFuture.supplyAsync(
  //           () -> comicDetailRepository.findAllWithCategories(pageable, filterCategoryIds))
  //       .thenCompose(
  //           comics -> {
  //             int pageSize = pageable.getPageSize();
  //             int currentElements = comics.getNumberOfElements();
  //             ConsoleUtils.prettyPrint(pageSize);
  //             ConsoleUtils.prettyPrint(currentElements);

  //             // If no additional comics are needed, return the result as-is
  //             if (currentElements == pageSize) {
  //               return CompletableFuture.completedFuture(
  //                   new PageImpl<>(comics.getContent(), pageable, comics.getTotalElements()));
  //             }

  //             // Otherwise, fetch the remaining comics from the next service
  //             int remainingSize = pageSize - currentElements;

  //             return getNextService()
  //                 .getComicsWithCategories(
  //                     PageRequest.of(0, remainingSize, pageable.getSort()), filterCategoryIds)
  //                 .thenApply(
  //                     nextComics -> {
  //                       // Combine the results
  //                       var combinedComics = new ArrayList<>(comics.getContent());
  //                       combinedComics.addAll(nextComics.getContent());

  //                       // Return a new PageImpl with combined data
  //                       return new PageImpl<>(
  //                           combinedComics,
  //                           pageable,
  //                           comics.getTotalElements() + nextComics.getTotalElements());
  //                     });
  //           });
  // }
}
