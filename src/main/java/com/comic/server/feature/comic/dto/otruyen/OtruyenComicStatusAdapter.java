package com.comic.server.feature.comic.dto.otruyen;

import com.comic.server.feature.comic.model.Comic.Status;

public class OtruyenComicStatusAdapter {

  private static final String NEW = "new";
  private static final String ONGOING = "ongoing";
  private static final String COMPLETED = "completed";
  private static final String COMMING_SOOM = "comming_soon";

  public static Status convertToStatus(String status) {
    if (status.equals(NEW)) {
      return Status.NEW;
    } else if (status.equals(ONGOING)) {
      return Status.ONGOING;
    } else if (status.equals(COMPLETED)) {
      return Status.COMPLETED;
    } else if (status.equals(COMMING_SOOM)) {
      return Status.COMMING_SOON;
    }
    return Status.UNKNOWN;
  }
}
