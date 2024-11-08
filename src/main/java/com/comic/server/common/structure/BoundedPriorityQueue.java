package com.comic.server.common.structure;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class BoundedPriorityQueue<T> extends PriorityQueue<T> {
  private final int maxSize;

  private Set<T> uniqueSet;

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
    if (!checkExists(t)) {
      return false;
    } else if (size() < maxSize) {
      return super.add(t);
    } else if (comparator().compare(t, peek()) > 0) {
      poll();
      return super.add(t);
    }
    return false;
  }

  private boolean checkExists(T t) {
    if (unique) {
      if (uniqueSet == null) {
        uniqueSet = new HashSet<>();
      }

      // add successful if the element is not already in the set
      return !uniqueSet.add(t);
    }
    return false;
  }
}
