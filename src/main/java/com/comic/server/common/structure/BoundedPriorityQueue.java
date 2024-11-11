package com.comic.server.common.structure;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import org.checkerframework.common.value.qual.IntRange;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class BoundedPriorityQueue<T> extends PriorityQueue<T> {
  private final int maxSize;
  private transient HashMap<T, Boolean> existsMap;
  private Strategy strategy;

  public enum Strategy {
    UNIQUE,
    UPDATE_IF_EXISTS,
    NORMAL
  }

  public Strategy getStrategy() {
    return strategy;
  }

  public void setStrategy(Strategy strategy) {
    this.strategy = strategy;
  }

  public BoundedPriorityQueue(@IntRange(from = 1) int maxSize, @Nullable Comparator<T> comparator) {
    this(maxSize, comparator, Strategy.NORMAL);
  }

  public BoundedPriorityQueue(
      @IntRange(from = 1) int maxSize, @Nullable Comparator<T> comparator, Strategy strategy) {
    this(maxSize, comparator, null, strategy);
  }

  public BoundedPriorityQueue(
      @IntRange(from = 1) int maxSize,
      @Nullable Comparator<T> comparator,
      @NonNull Collection<? extends T> elements) {
    this(maxSize, comparator, elements, Strategy.NORMAL);
  }

  public BoundedPriorityQueue(
      @IntRange(from = 1) int maxSize,
      @Nullable Comparator<T> comparator,
      @Nullable Collection<? extends T> elements,
      Strategy strategy) {
    super(maxSize, comparator);
    this.maxSize = maxSize;
    this.strategy = strategy;

    if (elements != null) {
      this.existsMap = new HashMap<>(maxSize);
      for (T element : elements) {
        add(element);
        existsMap.put(element, true);
      }
    }
  }

  @Override
  public boolean add(T t) {
    if (t == null) return false;
    else if (isExists(t)) return strategy == Strategy.UPDATE_IF_EXISTS && updateIfExists(t);
    else if (size() < maxSize) return offer(t);
    poll();
    return offer(t);
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean remove(Object o) {
    if (o == null) return false;
    untrackExistance((T) o);
    return super.remove(o);
  }

  @Override
  public boolean contains(Object o) {
    if (o == null) return false;
    return getExistsMap().containsKey(o);
  }

  @Override
  public boolean offer(T t) {
    if (t == null) return false;
    trackExistance(t);
    return super.offer(t);
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean removeAll(Collection<?> c) {
    for (Object o : c) untrackExistance((T) o);
    return super.removeAll(c);
  }

  @Override
  public T poll() {
    T t = super.poll();
    untrackExistance(t);
    return t;
  }

  @Override
  public void clear() {
    super.clear();
    getExistsMap().clear();
  }

  private HashMap<T, Boolean> getExistsMap() {
    if (existsMap == null) {
      existsMap = new HashMap<>(this.maxSize);
      for (T e : this) existsMap.put(e, true);
    }
    return existsMap;
  }

  private void trackExistance(T t) {
    getExistsMap().put(t, true);
  }

  private void untrackExistance(T t) {
    getExistsMap().remove(t);
  }

  /**
   * Replace the old element with the new element.
   *
   * @param T the new element
   */
  public boolean updateIfExists(T t) {
    return remove(t) && offer(t);
  }

  /**
   * Check if the element exists in the list.
   *
   * @param t the element to check
   * @return true if the element exists in the list, false otherwise
   */
  private boolean isExists(T t) {
    return strategy != Strategy.NORMAL && getExistsMap().containsKey(t);
  }
}
