package com.comic.server.common.structure;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import org.checkerframework.common.value.qual.IntRange;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

public class BoundedPriorityQueue<T> extends PriorityQueue<T> {
  private final int maxSize;

  private Set<T> uniqueSet;

  private Set<T> getUniqueSet() {
    if (uniqueSet == null) {
      uniqueSet = new HashSet<>();
      if (size() > 0) this.stream().forEach(uniqueSet::add);
    }
    return uniqueSet;
  }

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

  public BoundedPriorityQueue(@IntRange(from = 1) int maxSize, @NonNull Comparator<T> comparator) {
    this(maxSize, comparator, null);
  }

  public BoundedPriorityQueue(
      @IntRange(from = 1) int maxSize,
      @NonNull Comparator<T> comparator,
      @NonNull Collection<? extends T> elements) {
    this(maxSize, comparator, elements, false);
  }

  public BoundedPriorityQueue(
      @IntRange(from = 1) int maxSize,
      @NonNull Comparator<T> comparator,
      @NonNull Collection<? extends T> elements,
      boolean unique) {
    this(maxSize, comparator, elements, unique, false);
  }

  public BoundedPriorityQueue(
      int maxSize,
      Comparator<T> comparator,
      Collection<? extends T> elements,
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
    Assert.notNull(t, "Element must not be null");
    if (isExists(t)) {
      return updateIfExists && removeIf(e -> comparator().compare(e, t) == 0) && offer(t);
    } else if (size() < maxSize) {
      return offer(t);
    } else if (comparator().compare(t, peek()) > 0) {
      poll();
      return offer(t);
    }
    return false;
  }

  private boolean isExists(T t) {
    return unique && !getUniqueSet().add(t);
  }
}
