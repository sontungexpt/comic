package com.comic.server.feature.comic.service.impl;

import com.comic.server.exceptions.ResourceAlreadyInUseException;
import com.comic.server.exceptions.ResourceNotFoundException;
import com.comic.server.feature.comic.model.Comic;
import com.comic.server.feature.comic.model.chapter.AbstractChapter;
import com.comic.server.feature.comic.model.thirdparty.SourceName;
import com.comic.server.feature.comic.repository.ChapterRepository;
import com.comic.server.feature.comic.service.ChapterChainService;
import com.comic.server.feature.comic.service.ChapterService;
import com.comic.server.feature.comic.service.ComicService;
import com.comic.server.feature.user.model.User;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChapterServiceImpl implements ChapterService {

  private final ChapterRepository chapterRepository;
  private final OtruyenChapterServiceImpl otruyenChapterService;
  private final ComicService comicService;

  @Override
  @Transactional
  @Cacheable(
      value = "comic_chapter",
      key = "'chapterId:' + #chapterId" + " + '-comicId:' + #comicId")
  public AbstractChapter getChapterDetailById(String comicId, String chapterId) {
    Comic comic = comicService.getComicById(comicId);
    return fetchChapterDetail(comic, chapterId);
  }

  @Override
  @Transactional
  public AbstractChapter createChapter(AbstractChapter chapter, User user) {
    Comic comic = comicService.getComicById(chapter.getComicId());
    if (!comic.couldBeEditBy(user.getId())) {
      throw new AccessDeniedException("You don't have permission to create chapter for this comic");
    }

    try {
      return chapterRepository.save(chapter);
    } catch (DuplicateKeyException e) {
      throw new ResourceAlreadyInUseException(
          AbstractChapter.class, Map.of("comicId", chapter.getComicId(), "num", chapter.getNum()));
    }
  }

  @Override
  public ChapterChainService getNextService() {
    return otruyenChapterService;
  }

  @Override
  public AbstractChapter fetchChapterDetail(Comic comic, String chapterId) {
    if (canHandle(comic.getThirdPartySource().getNameAsSourceName())) {
      return chapterRepository
          .findById(chapterId)
          .orElseThrow(() -> new ResourceNotFoundException(AbstractChapter.class, "id", chapterId));
    } else {
      return getNextService().fetchChapterDetail(comic, chapterId);
    }
  }

  @Override
  public boolean canHandle(SourceName sourceName) {
    return sourceName == SourceName.ROOT;
  }
}
