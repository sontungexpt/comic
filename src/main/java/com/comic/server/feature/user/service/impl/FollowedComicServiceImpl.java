package com.comic.server.feature.user.service.impl;

import com.comic.server.feature.comic.dto.ComicDTO;
import com.comic.server.feature.user.model.FollowedComic;
import com.comic.server.feature.user.repository.CustomFollowedComicRepository;
import com.comic.server.feature.user.repository.FollowedComicRepository;
import com.comic.server.feature.user.service.FollowedComicService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public record FollowedComicServiceImpl(
    FollowedComicRepository followedComicRepository,
    CustomFollowedComicRepository customFollowedComicRepository)
    implements FollowedComicService {

  @Override
  public void followComic(String userId, String comicId) {
    followedComicRepository.save(new FollowedComic(userId, comicId));
  }

  @Override
  public void unfollowComic(String userId, String comicId) {
    followedComicRepository.deleteByUserIdAndComicId(userId, comicId);
  }

  @Override
  public Page<ComicDTO> getFollowedComics(String userId, Pageable pageable) {
    return customFollowedComicRepository.findByUserId(userId, pageable);
  }
}
