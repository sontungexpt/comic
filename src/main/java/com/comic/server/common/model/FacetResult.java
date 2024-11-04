package com.comic.server.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacetResult<T> {

  private List<T> dataFacet;

  private List<Map<String, Object>> countFacet;

  @JsonCreator
  public FacetResult(List<T> dataFacet, List<Map<String, Object>> countFacet) {
    this.dataFacet = dataFacet;
    this.countFacet = countFacet;
  }

  public static final String getDataFacetName() {
    return "dataFacet";
  }

  public static final String getCountFacetName() {
    return "countFacet";
  }

  public List<T> getDatas() {
    return dataFacet;
  }

  public int getCount(String key) {
    if (countFacet == null || countFacet.isEmpty()) {
      return 0;
    } else {
      Object count = countFacet.get(0).get(key);
      if (count == null) {
        return 0;
      }
      return (int) count;
    }
  }
}
