package com.comic.server.feature.comic.service;

import com.comic.server.feature.comic.model.chapter.Chapter;

public interface ChapterService {

  Chapter getChapterDetailById(String id);

  Chapter createChapter(Chapter chapter);
}
