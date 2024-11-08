// package com.comic.server.common.payload;

// import java.util.Iterator;
// import java.util.List;
// import java.util.function.Function;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Sort;

// @lombok.Getter
// @lombok.Setter
// @lombok.Builder
// @lombok.AllArgsConstructor
// public class PageResponse<T> implements Page<T> {

//   private List<T> content;
//   private int totalPages;
//   private long totalItems;

//   private int currentPage;
//   private int pageSize;
//   private int numberOfElements;

//   private boolean isFirst;
//   private boolean isLast;
//   private boolean empty;
//   private boolean sorted;
//   private Sort sort;

//   @Override
//   public int getNumber() {
//     return currentPage;
//   }

//   @Override
//   public int getSize() {
//     return pageSize;
//   }

//   @Override
//   public boolean hasContent() {
//     return !content.isEmpty();
//   }

//   @Override
//   public Sort getSort() {
//     return sort;
//   }

//   @Override
//   public boolean isFirst() {
//     return firstPage;
//   }

//   @Override
//   public boolean isLast() {
//     // TODO Auto-generated method stub
//     throw new UnsupportedOperationException("Unimplemented method 'isLast'");
//   }

//   @Override
//   public boolean hasNext() {
//     // TODO Auto-generated method stub
//     throw new UnsupportedOperationException("Unimplemented method 'hasNext'");
//   }

//   @Override
//   public boolean hasPrevious() {
//     // TODO Auto-generated method stub
//     throw new UnsupportedOperationException("Unimplemented method 'hasPrevious'");
//   }

//   @Override
//   public Pageable nextPageable() {
//     // TODO Auto-generated method stub
//     throw new UnsupportedOperationException("Unimplemented method 'nextPageable'");
//   }

//   @Override
//   public Pageable previousPageable() {
//     // TODO Auto-generated method stub
//     throw new UnsupportedOperationException("Unimplemented method 'previousPageable'");
//   }

//   @Override
//   public Iterator<T> iterator() {
//     // TODO Auto-generated method stub
//     throw new UnsupportedOperationException("Unimplemented method 'iterator'");
//   }

//   @Override
//   public long getTotalElements() {
//     return totalItems;
//   }

//   @Override
//   public <U> Page<U> map(Function<? super T, ? extends U> converter) {
//     // TODO Auto-generated method stub
//     throw new UnsupportedOperationException("Unimplemented method 'map'");
//   }
// }
