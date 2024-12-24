package com.comic.server.feature.history.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;

@AllArgsConstructor
@Getter
@Setter
public class ReadChapter {

  @NonNull private ObjectId chapterId;

  @NonNull private Instant readTime;

  @Override
  public int hashCode() {
    return chapterId.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    else if (o instanceof ReadChapter that) {
      return chapterId.equals(that.chapterId);
    }
    return false;
  }
}
