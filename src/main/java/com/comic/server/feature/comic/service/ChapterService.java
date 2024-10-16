package com.comic.server.feature.comic.service;

import com.comic.server.feature.comic.model.chapter.AbstractChapter;

public interface ChapterService {

  AbstractChapter getChapterDetailById(String id);

  AbstractChapter createChapter(AbstractChapter chapter);
}
