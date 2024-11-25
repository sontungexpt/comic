package com.comic.server.feature.user.controller;

import com.comic.server.annotation.CurrentUser;
import com.comic.server.feature.user.dto.UpdateUserProfileRequest;
import com.comic.server.feature.user.dto.UserProfile;
import com.comic.server.feature.user.model.User;
import com.comic.server.feature.user.repository.UserRepository;
import com.comic.server.feature.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
// @RolesAllowed(RoleType.Fields.CUSTOMER)
public record UserController(UserService userService, UserRepository userRepository) {

  @GetMapping("")
  public ResponseEntity<UserProfile> getUserProfile(@CurrentUser User user) {
    return ResponseEntity.ok(userService.getUserProfile(user));
  }

  @PatchMapping(path = "", consumes = "application/merge-patch+json")
  public ResponseEntity<UserProfile> updateUserProfile(
      @CurrentUser User user, @RequestBody UpdateUserProfileRequest request) {
    return ResponseEntity.ok(userService.updateUserProfile(user, request));
  }
}
