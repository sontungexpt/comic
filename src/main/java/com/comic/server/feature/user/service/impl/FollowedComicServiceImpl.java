package com.comic.server.feature.user.service.impl;

import com.comic.server.exceptions.ResourceAlreadyInUseException;
import com.comic.server.exceptions.ResourceNotFoundException;
import com.comic.server.feature.comic.dto.ComicDTO;
import com.comic.server.feature.user.model.FollowedComic;
import com.comic.server.feature.user.repository.CustomFollowedComicRepository;
import com.comic.server.feature.user.repository.FollowedComicRepository;
import com.comic.server.feature.user.service.FollowedComicService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FollowedComicServiceImpl implements FollowedComicService {
  private final FollowedComicRepository followedComicRepository;
  private final CustomFollowedComicRepository customFollowedComicRepository;

  @Override
  @CacheEvict(value = "comics", allEntries = true)
  public void followComic(String userId, String comicId) {
    try {
      followedComicRepository.insert(new FollowedComic(userId, comicId));
    } catch (DuplicateKeyException e) {
      throw new ResourceAlreadyInUseException(
          FollowedComic.class, Map.of("userId", userId, "comicId", comicId));
    }
  }

  @Override
  @CacheEvict(value = "comics", allEntries = true)
  public void unfollowComic(String userId, String comicId) {
    int count =
        followedComicRepository.deleteByUserIdAndComicId(
            new ObjectId(userId), new ObjectId(comicId));
    if (count == 0) {
      throw new ResourceNotFoundException(
          FollowedComic.class, Map.of("userId", userId, "comicId", comicId));
    }
  }

  @Override
  @Cacheable(
      value = "followed_comics",
      key =
          "'user:' + #userId"
              + "+ 'page:' + #pageable.pageNumber"
              + "+ 'size:' + #pageable.pageSize"
              + "+ 'sort:' + #pageable.sort")
  public Page<ComicDTO> getFollowedComics(String userId, Pageable pageable) {
    return customFollowedComicRepository.findByUserId(userId, pageable);
  }

  @Override
  public boolean isUserFollowingComic(String userId, String comicId) {
    return followedComicRepository.existsByUserIdAndComicId(
        new ObjectId(userId), new ObjectId(comicId));
  }

  @Override
  public Page<ComicDTO> searchFollowedComics(String keyword, String userId, Pageable pageable) {
    return customFollowedComicRepository.searchFollowedComic(keyword, userId, pageable);
  }
}
