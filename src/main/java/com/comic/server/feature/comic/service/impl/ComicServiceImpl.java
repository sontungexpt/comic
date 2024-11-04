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
import com.comic.server.feature.user.model.User;
import com.comic.server.utils.SortUtils;
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
  public long countComics(List<String> filterCategoryIds) {
    return comicDetailRepository.countComicDTOs(filterCategoryIds);
  }

  @Override
  @Cacheable(
      value = "comic",
      key =
          "'comic_user:' + #comicId + #user?.id"
              + "+ 'page:' + #pageable.pageNumber"
              + "+ 'size:' + #pageable.pageSize"
              + "+ 'sort:' + #pageable.sort")
  public ComicDetailDTO getComicDetail(
      String comicId, SourceName sourceName, Pageable pageable, User user) {
    if (sourceName == SourceName.ROOT) {
      return comicDetailRepository.findComicDetail(comicId, pageable, user);
    } else {
      return getNextService().getComicDetail(comicId, sourceName, pageable, user);
    }
  }

  @Override
  @Cacheable(value = "comic_chapters", key = "'comicId:' + #comicId")
  public List<ShortInfoChapter> getChaptersByComicId(String comicId) {
    if (comicRepository.existsByIdAndThirdPartySourceName(comicId, SourceName.ROOT)) {
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
          "'page:' + #pageable.pageNumber "
              + "+ 'size:' + #pageable.pageSize "
              + "+ 'sort:' + #pageable.sort "
              + "+ 'filter:' + #filterCategoryIds?.toString()")
  @Override
  public Page<ComicDTO> getComicsWithCategories(Pageable pageable, List<String> filterCategoryIds) {

    Page<ComicDTO> comics =
        comicDetailRepository.findAllWithCategories(pageable, filterCategoryIds);

    int pageSize = pageable.getPageSize();
    int currentNumberOfElements = comics.getNumberOfElements();

    if (currentNumberOfElements < pageSize || comics.isLast()) {
      log.info("Fetching more comics from next service");

      int remainingSize = pageSize - currentNumberOfElements;

      Page<ComicDTO> nextComics =
          getNextService()
              .getComicsWithCategories(
                  PageRequest.of(0, remainingSize, pageable.getSort()), filterCategoryIds);

      var returnComics = new HashSet<>(comics.getContent());
      returnComics.addAll(nextComics.getContent());

      return new PageImpl<>(
          SortUtils.sort(new ArrayList<>(returnComics), pageable).stream().limit(pageSize).toList(),
          pageable,
          comics.getTotalElements() + nextComics.getTotalElements());
    }

    return new PageImpl<>(
        comics.getContent(),
        pageable,
        comics.getTotalElements() + getNextService().countComics(filterCategoryIds));
  }

  @Override
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
          SortUtils.sort(new ArrayList<>(returnComics), pageable).stream().limit(pageSize).toList(),
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
}
