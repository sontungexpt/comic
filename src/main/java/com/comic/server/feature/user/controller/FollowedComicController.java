package com.comic.server.feature.user.controller;

import com.comic.server.annotation.CurrentUser;
import com.comic.server.annotation.PageableQueryParams;
import com.comic.server.annotation.PublicEndpoint;
import com.comic.server.config.OpenApiConfig;
import com.comic.server.feature.user.enums.RoleType;
import com.comic.server.feature.user.model.User;
import com.comic.server.feature.user.service.FollowedComicService;
import com.comic.server.utils.PrincipalUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/comics/followed")
@RestController
@Tag(name = "Followed Comic")
public class FollowedComicController {

  private final FollowedComicService followedComicService;

  @Operation(summary = "Follow a comic")
  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_NAME)
  @PostMapping("/{comicId}")
  @RolesAllowed(RoleType.Fields.READER)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void followComic(@PathVariable("comicId") String comicId, @CurrentUser User user) {
    followedComicService.followComic(user.getId(), comicId);
  }

  @Operation(summary = "Unfollow a comic")
  @DeleteMapping("/{comicId}")
  @RolesAllowed(RoleType.Fields.READER)
  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_NAME)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void unfollowComic(@PathVariable("comicId") String comicId, @CurrentUser User user) {
    followedComicService.unfollowComic(user.getId(), comicId);
  }

  @Operation(summary = "Get followed comics of current user")
  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_NAME)
  @GetMapping("")
  @PageableQueryParams
  @RolesAllowed(RoleType.Fields.READER)
  public ResponseEntity<?> getFollowedComics(
      @PageableDefault(size = 24, page = 0) @Parameter(hidden = true) Pageable pageable) {
    return ResponseEntity.ok(
        followedComicService.getFollowedComics(PrincipalUtils.getUserId(), pageable));
  }

  @Operation(summary = "Search followed comics of current user")
  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_NAME)
  @GetMapping("/searching")
  @PageableQueryParams
  @Parameter(name = "sort", hidden = true)
  public ResponseEntity<?> searchFollowedComics(
      @RequestParam String q,
      @CurrentUser User user,
      @PageableDefault(size = 24, page = 0) Pageable pageable) {
    return ResponseEntity.ok(followedComicService.searchFollowedComics(q, user.getId(), pageable));
  }

  @Operation(summary = "Check if a comic is followed by current user")
  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_NAME)
  @GetMapping("/{comicId}/follow-status")
  @PublicEndpoint(filterJwt = true)
  public boolean isComicFollowed(String comicId, @CurrentUser User user) {
    if (user == null) return false;
    return followedComicService.isUserFollowingComic(user.getId(), comicId);
  }
}
