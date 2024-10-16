package com.comic.server.common.structure;

import java.util.Comparator;
import java.util.PriorityQueue;
import org.springframework.util.Assert;

public class BoundedPriorityQueue<T> extends PriorityQueue<T> {
  private final int maxSize;

  public BoundedPriorityQueue(int maxSize, Comparator<T> comparator) {
    super(maxSize, comparator);
    this.maxSize = maxSize;
  }

  public BoundedPriorityQueue(
      int maxSize, Comparator<T> comparator, Iterable<? extends T> elements) {
    this(maxSize, comparator);
    Assert.notNull(elements, "Elements must not be null");
    for (T element : elements) {
      add(element);
    }
  }

  @Override
  public boolean add(T t) {
    if (size() < maxSize) {
      return super.add(t);
    } else if (comparator().compare(t, peek()) > 0) {
      poll();
      return super.add(t);
    }
    return false;
  }
}
