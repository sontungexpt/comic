package com.comic.server.feature.comic.model.thirdparty;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.HashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class OtruyenMetadata extends AbstractThirdPartyMetadata {

  @AllArgsConstructor
  @NoArgsConstructor
  @Getter
  @Setter
  public static class PaginationMetadata {

    private long totalComics = 0;

    public void setTotalComics(long totalComics) {
      this.totalComics = totalComics;
      this.updatedAt = Instant.now();
    }

    private long totalItemsPerPage = 0;

    public void setTotalItemsPerPage(long totalItemsPerPage) {
      this.totalItemsPerPage = totalItemsPerPage;
      this.updatedAt = Instant.now();
    }

    private long currentSyncedPage = 0;

    public void setCurrentSyncedPage(long currentSyncedPage) {
      this.currentSyncedPage = currentSyncedPage;
      this.updatedAt = Instant.now();
    }

    private Instant updatedAt = Instant.now();

    public long getNextPage() {
      if (totalComics == 0 && totalItemsPerPage == 0 && currentSyncedPage == 0) {
        return 1; // First time sync
      } else if (currentSyncedPage * totalItemsPerPage < totalComics) {
        return currentSyncedPage + 1;
      } else if (Instant.now().minusSeconds(10 * 60 * 60 * 24).isAfter(updatedAt)) {
        // If the last update is more than 10 days ago, we should sync again
        return currentSyncedPage + 1;
      }
      return -1;
    }
  }

  private long totalSyncedItems = 0;

  public void incrementTotalSyncedItems(long totalSyncedItems) {
    this.totalSyncedItems += totalSyncedItems;
  }

  private PaginationMetadata comicPagination = new PaginationMetadata();

  @Schema(
      description =
          "Map of category to pagination metadata. Key is slug of category, value is pagination"
              + " metadata")
  private HashMap<String, PaginationMetadata> categoryPaginationMap = new HashMap<>();

  public PaginationMetadata getCategoryPagination(String category) {
    if (categoryPaginationMap.get(category) == null) {
      categoryPaginationMap.put(category, new PaginationMetadata());
    }
    return categoryPaginationMap.get(category);
  }

  public void setCategoryPagination(String category, PaginationMetadata paginationMetadata) {
    categoryPaginationMap.put(category, paginationMetadata);
  }

  public OtruyenMetadata() {
    super(SourceName.OTRUYEN);
  }
}
