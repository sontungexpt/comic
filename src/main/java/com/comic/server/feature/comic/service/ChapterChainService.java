package com.comic.server.feature.comic.service;

import com.comic.server.feature.comic.model.Comic;
import com.comic.server.feature.comic.model.chapter.AbstractChapter;
import com.comic.server.feature.comic.model.thirdparty.SourceName;

/** ChapterChainService */
public interface ChapterChainService {

  AbstractChapter fetchChapterDetail(Comic comic, String chapterId);

  ChapterChainService getNextService();

  boolean canHandle(SourceName sourceName);
}
