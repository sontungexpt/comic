package com.comic.server.common.structure;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class BoundedPriorityQueue<T> extends PriorityQueue<T> {
  private final int maxSize;

  private Map<Integer, T> indexMap;

  private boolean unique = false;

  public BoundedPriorityQueue(int maxSize, Comparator<T> comparator) {
    this(maxSize, comparator, null);
  }

  public BoundedPriorityQueue(
      int maxSize, Comparator<T> comparator, Iterable<? extends T> elements) {
    this(maxSize, comparator, elements, false);
  }

  public BoundedPriorityQueue(
      int maxSize, Comparator<T> comparator, Iterable<? extends T> elements, boolean unique) {
    super(maxSize, comparator);
    this.maxSize = maxSize;
    this.unique = unique;
    if (elements != null) {
      for (T element : elements) {
        add(element);
      }
    }
  }

  @Override
  public boolean add(T t) {
    if (unique && indexMap != null && indexMap.containsKey(t.hashCode())) {
      return false;
    } else if (size() < maxSize) {
      trackIndex(t);
      return super.add(t);
    } else if (comparator().compare(t, peek()) > 0) {
      poll();
      trackIndex(t);
      return super.add(t);
    }
    return false;
  }

  private void trackIndex(T t) {
    if (indexMap == null) {
      indexMap = new HashMap<>();
    }
    if (unique) {
      indexMap.put(t.hashCode(), t);
    }
  }
}
