package com.comic.server.feature.comic.service;

import com.comic.server.feature.comic.model.chapter.AbstractChapter;

public interface ChapterService extends ChapterChainService {

  AbstractChapter createChapter(AbstractChapter chapter);

  AbstractChapter getChapterDetailById(String comicId, String chapterId);
}
