package com.comic.server.feature.comic.service;

import com.comic.server.feature.comic.model.chapter.AbstractChapter;
import com.comic.server.feature.user.model.User;
import org.springframework.lang.NonNull;

public interface ChapterService extends ChapterChainService {

  AbstractChapter createChapter(@NonNull AbstractChapter chapter, @NonNull User user);

  AbstractChapter getChapterDetailById(@NonNull String comicId, @NonNull String chapterId);
}
