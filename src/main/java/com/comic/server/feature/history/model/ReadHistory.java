package com.comic.server.feature.history.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "read_histories")
@NoArgsConstructor
@Getter
@Setter
@CompoundIndex(name = "user_comic", def = "{'userId': 1, 'comicId': 1}", unique = true)
public class ReadHistory {

  @Id private String id;

  @NonNull private ObjectId userId;

  @NonNull private ObjectId comicId;

  @NonNull private ReadChapter lastestReadChapter;

  @Setter(lombok.AccessLevel.NONE)
  private List<ReadChapter> readChapters = new ArrayList<>();

  public void addChapterReadHistory(ReadChapter chapterRead) {
    lastestReadChapter = chapterRead;

    var index = readChapters.indexOf(chapterRead);
    if (index != -1) {
      readChapters.set(index, chapterRead);
    } else {
      readChapters.add(chapterRead);
    }
  }

  @CreatedDate private Instant createdAt;

  @LastModifiedDate private Instant updatedAt;
}