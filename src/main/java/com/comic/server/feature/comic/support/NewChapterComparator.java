package com.comic.server.feature.comic.support;

import com.comic.server.feature.comic.model.chapter.Chapter;
import java.util.Comparator;

public class NewChapterComparator implements Comparator<Chapter> {

  @Override
  public int compare(Chapter o1, Chapter o2) {

    // update after 3 hours is considered new and be prioritized
    if (o1.getUpdatedAt().minusSeconds(60 * 60 * 3).isAfter(o2.getUpdatedAt())) {
      return 1;
    } else {
      return o1.getNum().compareTo(o2.getNum());
    }
  }
}
