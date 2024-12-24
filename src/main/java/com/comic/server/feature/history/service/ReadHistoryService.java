package com.comic.server.feature.history.service;

import com.comic.server.feature.history.model.ReadChapter;
import com.comic.server.feature.history.model.ReadHistory;
import java.util.List;
import java.util.Optional;

public interface ReadHistoryService {

  Optional<ReadChapter> getLatestReadChapter(String userId, String comicId);

  ReadHistory addReadHistory(String userId, String comicId, String chapterId);

  ReadHistory findReadHistory(String userId, String comicId);

  void removeReadHistory(String userId, String comicId, String chapterId);

  void removeReadHistory(String userId, String comicId);

  void removeReadHistory(String userId);

  void removeReadHistory(String userId, List<String> comicIds);

  List<ReadChapter> getReadChapters(String userId, String comicId);
}
