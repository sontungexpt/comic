package com.comic.server.feature.comic.service;

import com.comic.server.feature.comic.model.chapter.AbstractChapter;
import com.comic.server.feature.user.model.User;

public interface ChapterService extends ChapterChainService {

  AbstractChapter createChapter(AbstractChapter chapter, User user);

  AbstractChapter getChapterDetailById(String comicId, String chapterId);
}
