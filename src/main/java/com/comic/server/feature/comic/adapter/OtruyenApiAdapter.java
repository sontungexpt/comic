package com.comic.server.feature.comic.adapter;

import com.comic.server.feature.comic.dto.ComicDTO;
import com.comic.server.feature.comic.dto.ComicDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OtruyenApiAdapter implements ApiAdapter {

  private String BASEURL = "https://otruyen.com";

  @Override
  public Page<ComicDTO> search(String keyword, Pageable pageable) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'search'");
  }

  @Override
  public ComicDetailDTO getComicDetail(String comicId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getComicDetail'");
  }

  @Override
  public void handleAutoImport() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'handleAutoImport'");
  }
}
