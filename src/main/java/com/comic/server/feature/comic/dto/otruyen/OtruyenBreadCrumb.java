package com.comic.server.feature.comic.dto.otruyen;

import lombok.Data;

@Data
public class OtruyenBreadCrumb {
  private String name;
  private String slug;
  private int position;
  private boolean isCurrent;
}
