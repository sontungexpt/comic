package com.comic.server.feature.comic.model.thirdparty;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtruyenMetadata extends AbstractThirdPartyMetadata {

  private long totalComics;

  private long totalItemsPerPage;

  private long currentSyncedPage;

  public long getNextPage() {
    if (createdAt == null) {
      return 1; // First time sync
    } else if (currentSyncedPage * totalItemsPerPage < totalComics) {
      return currentSyncedPage + 1;
    } else if (Instant.now().minusSeconds(10 * 60 * 60 * 24).isAfter(updatedAt)) {
      // If the last update is more than 10 days ago, we should sync again
      return currentSyncedPage;
    }
    return -1;
  }

  public OtruyenMetadata() {
    super(SourceName.OTRUYEN);
    this.totalComics = 0;
    this.totalItemsPerPage = 0;
    this.currentSyncedPage = 0;
  }
}
