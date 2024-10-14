package com.comic.server.feature.user.controller;

import com.comic.server.annotation.CurrentUser;
import com.comic.server.config.OpenApiConfig;
import com.comic.server.feature.user.enums.RoleType;
import com.comic.server.feature.user.model.User;
import com.comic.server.feature.user.service.FollowedComicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/comics/followed")
@RestController
public class FollowedComicController {

  private final FollowedComicService followedComicService;

  @Operation(summary = "Follow a comic")
  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_NAME)
  @PostMapping("/{comicId}")
  @RolesAllowed(RoleType.Fields.READER)
  public void followComic(@PathVariable("comicId") String comicId, @CurrentUser User user) {
    followedComicService.followComic(user.getId(), comicId);
  }

  @Operation(summary = "Unfollow a comic")
  @DeleteMapping("/{comicId}")
  @RolesAllowed(RoleType.Fields.READER)
  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_NAME)
  public void unfollowComic(@PathVariable("comicId") String comicId, @CurrentUser User user) {
    followedComicService.unfollowComic(user.getId(), comicId);
  }

  @Operation(summary = "Get followed comics of current user")
  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_NAME)
  @RolesAllowed(RoleType.Fields.READER)
  @GetMapping("")
  public ResponseEntity<?> getFollowedComics(
      @CurrentUser User user,
      @PageableDefault(size = 20, page = 0, sort = "rating", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return ResponseEntity.ok(followedComicService.getFollowedComics(user.getId(), pageable));
  }
}
