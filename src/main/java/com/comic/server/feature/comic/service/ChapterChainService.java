package com.comic.server.feature.comic.service;

import com.comic.server.feature.comic.model.chapter.AbstractChapter;

/** ChapterChainService */
public interface ChapterChainService {

  AbstractChapter getChapterDetailById(String comicId, String chapterId);

  ChapterChainService getNextService();
}
