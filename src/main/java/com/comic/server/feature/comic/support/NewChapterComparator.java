package com.comic.server.feature.comic.support;

import com.comic.server.feature.comic.model.chapter.Chapter;
import java.util.Comparator;

public class NewChapterComparator implements Comparator<Chapter> {

  @Override
  public int compare(Chapter o1, Chapter o2) {
    if (o1.equals(o2)) {
      return 0;
    } else if (o1.getUpdatedAt() != null
        && o2.getUpdatedAt() != null
        && o1.getUpdatedAt().minusSeconds(60 * 60 * 3).isAfter(o2.getUpdatedAt())) {
      // update after 3 hours is considered new and be prioritized
      return 1;
    } else {
      return o1.getNum().compareTo(o2.getNum());
    }
  }
}
