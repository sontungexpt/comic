package com.comic.server.feature.comic.support;

import com.comic.server.feature.comic.model.chapter.Chapter;
import java.util.Comparator;

public class NewChapterComparator implements Comparator<Chapter> {

  @Override
  public int compare(Chapter o1, Chapter o2) {
    if (o1 == o2) {
      return 0;
    } else if (o1 == null || o2 == null) {
      return 0;
    } else {
      return o1.getNum().compareTo(o2.getNum());
    }
  }
}
