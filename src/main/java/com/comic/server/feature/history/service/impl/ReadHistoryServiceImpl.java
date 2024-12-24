package com.comic.server.feature.history.service.impl;

import com.comic.server.exceptions.ResourceNotFoundException;
import com.comic.server.feature.history.model.ReadChapter;
import com.comic.server.feature.history.model.ReadHistory;
import com.comic.server.feature.history.repository.ReadHistoryRepository;
import com.comic.server.feature.history.service.ReadHistoryService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReadHistoryServiceImpl implements ReadHistoryService {

  private final ReadHistoryRepository readHistoryRepository;

  @Override
  public ReadHistory addReadHistory(String userId, String comicId, String chapterId) {
    ReadHistory readHistory =
        readHistoryRepository
            .findByUserIdAndComicId(new ObjectId(userId), new ObjectId(comicId))
            .orElseGet(() -> new ReadHistory());

    readHistory.setUserId(new ObjectId(userId));
    readHistory.setComicId(new ObjectId(comicId));

    ReadChapter chapterRead = new ReadChapter(new ObjectId(chapterId), Instant.now());
    readHistory.addChapterReadHistory(chapterRead);
    return readHistoryRepository.save(readHistory);
  }

  @Override
  public void removeReadHistory(String userId, String comicId, String chapterId) {
    ReadHistory readHistory = findReadHistory(userId, comicId);

    readHistory
        .getReadChapters()
        .removeIf(chapterRead -> chapterRead.getChapterId().toHexString().equals(chapterId));

    if (readHistory.getLastestReadChapter().getChapterId().toHexString().equals(chapterId)) {
      readHistory.getReadChapters().stream()
          .max((o1, o2) -> o1.getReadTime().compareTo(o2.getReadTime()))
          .ifPresent(readHistory::setLastestReadChapter);
    }

    readHistoryRepository.save(readHistory);
  }

  @Override
  public void removeReadHistory(String userId, String comicId) {
    readHistoryRepository.deleteByUserIdAndComicId(new ObjectId(userId), new ObjectId(comicId));
  }

  @Override
  public void removeReadHistory(String userId) {
    readHistoryRepository.deleteByUserId(new ObjectId(userId));
  }

  @Override
  public void removeReadHistory(String userId, List<String> comicIds) {
    readHistoryRepository.findByUserId(new ObjectId(userId)).stream()
        .filter(readHistory -> comicIds.contains(readHistory.getComicId().toHexString()))
        .forEach(readHistoryRepository::delete);
  }

  @Override
  public Optional<ReadChapter> getLatestReadChapter(String userId, String comicId) {
    ReadHistory readHistory =
        readHistoryRepository
            .findByUserIdAndComicId(new ObjectId(userId), new ObjectId(comicId))
            .orElse(null);
    if (readHistory == null) {
      return Optional.empty();
    }
    return Optional.of(readHistory.getLastestReadChapter());
  }

  @Override
  public ReadHistory findReadHistory(String userId, String comicId) {
    return readHistoryRepository
        .findByUserIdAndComicId(new ObjectId(userId), new ObjectId(comicId))
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    ReadHistory.class, Map.of("userId", userId, "comicId", comicId)));
  }

  @Override
  public List<ReadChapter> getReadChapters(String userId, String comicId) {
    if (userId == null) return new ArrayList<>();
    ReadHistory readHistory =
        readHistoryRepository
            .findByUserIdAndComicId(new ObjectId(userId), new ObjectId(comicId))
            .orElse(null);
    if (readHistory == null) {
      return new ArrayList<>();
    }
    return readHistory.getReadChapters();
  }
}
