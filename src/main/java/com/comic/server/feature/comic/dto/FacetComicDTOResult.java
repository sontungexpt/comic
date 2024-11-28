package com.comic.server.feature.comic.dto;

import com.comic.server.common.model.FacetResult;
import java.util.List;
import java.util.Map;

public class FacetComicDTOResult extends FacetResult<ComicDTO> {

  public FacetComicDTOResult(List<ComicDTO> dataFacet, List<Map<String, Object>> countFacet) {
    super(dataFacet, countFacet);
  }
}
