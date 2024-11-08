package com.comic.server.common.structure;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class BoundedPriorityQueue<T> extends PriorityQueue<T> {
  private final int maxSize;

  private Set<T> uniqueSet;

  private boolean unique = false;

  private boolean updateIfExists = false;

  public boolean isUnique() {
    return unique;
  }

  public void setUnique(boolean unique) {
    this.unique = unique;
  }

  public boolean isUpdateIfExists() {
    return updateIfExists;
  }

  public void setUpdateIfExists(boolean updateIfExists) {
    this.updateIfExists = updateIfExists;
  }

  public BoundedPriorityQueue(int maxSize, Comparator<T> comparator) {
    this(maxSize, comparator, null);
  }

  public BoundedPriorityQueue(
      int maxSize, Comparator<T> comparator, Iterable<? extends T> elements) {
    this(maxSize, comparator, elements, false);
  }

  public BoundedPriorityQueue(
      int maxSize, Comparator<T> comparator, Iterable<? extends T> elements, boolean unique) {
    this(maxSize, comparator, elements, unique, false);
  }

  public BoundedPriorityQueue(
      int maxSize,
      Comparator<T> comparator,
      Iterable<? extends T> elements,
      boolean unique,
      boolean updateIfExists) {
    super(maxSize, comparator);
    this.maxSize = maxSize;
    this.unique = unique;
    this.updateIfExists = updateIfExists;
    if (elements != null) {
      for (T element : elements) {
        add(element);
      }
    }
  }

  @Override
  public boolean add(T t) {
    if (isExists(t)) {
      if (updateIfExists) {
        removeIf(e -> comparator().compare(t, e) == 0);
        return offer(t);
      }
      return false;
    } else if (size() < maxSize) {
      return offer(t);
    } else if (comparator().compare(t, peek()) > 0) {
      poll();
      return offer(t);
    }
    return false;
  }

  private boolean isExists(T t) {
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
