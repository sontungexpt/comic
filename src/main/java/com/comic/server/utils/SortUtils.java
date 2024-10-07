package com.comic.server.utils;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.ReflectionUtils;

public class SortUtils {

  public static <T> List<T> sort(List<T> list, Pageable pageable) {
    Sort sort = pageable.getSort();

    if (sort.isSorted()) {
      @SuppressWarnings({"unchecked", "rawtypes"})
      Comparator<T> comparator =
          sort.stream()
              .map(
                  order -> {
                    Comparator<T> orderComparator;
                    String propertyName = order.getProperty();
                    Field field = ReflectionUtils.findField(list.get(0).getClass(), propertyName);
                    if (field == null) {
                      return Comparator.comparing((T fdi) -> 0);
                    }
                    field.setAccessible(true);
                    orderComparator =
                        Comparator.comparing(
                            (T fdi) -> {
                              try {
                                return (Comparable) field.get(fdi);
                              } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                              }
                            });
                    return order.isAscending() ? orderComparator : orderComparator.reversed();
                  })
              .reduce(Comparator::thenComparing)
              .orElse((o1, o2) -> 0);
      list.sort(comparator);
    }
    return list;
  }
}
