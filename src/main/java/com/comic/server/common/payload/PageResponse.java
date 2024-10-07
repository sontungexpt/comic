package com.comic.server.common.payload;

import java.util.List;
import org.springframework.data.domain.Page;

@lombok.Getter
@lombok.Setter
@lombok.Builder
@lombok.AllArgsConstructor
public class PageResponse<T> {

  private List<T> content;
  private int totalPages;
  private long totalItems;

  private int currentPage;
  private int pageSize;
  private int numberOfElements;

  private boolean lastPage;
  private boolean firstPage;
  private boolean empty;
  private boolean sorted;
  private String sortBy;

  public PageResponse(Page<T> page) {
    this.content = page.getContent();
    this.totalPages = page.getTotalPages();
    this.totalItems = page.getTotalElements();

    this.currentPage = page.getNumber();
    this.pageSize = page.getSize();
    this.numberOfElements = page.getNumberOfElements();

    this.lastPage = page.isLast();
    this.firstPage = page.isFirst();
    this.empty = page.isEmpty();
    this.sorted = page.getSort().isSorted();
    this.sortBy = page.getSort().toString();
  }
}
