package com.comic.server.feature.user.service;

import com.comic.server.feature.comic.dto.ComicDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowedComicService {

  void followComic(String userId, String comicId);

  void unfollowComic(String userId, String comicId);

  Page<ComicDTO> getFollowedComics(String userId, Pageable pageable);

  Page<ComicDTO> searchFollowedComics(String keyword, String userId, Pageable pageable);

  boolean isUserFollowingComic(String userId, String comicId);
}
