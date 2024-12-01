package com.comic.server.feature.user.controller;

import com.comic.server.annotation.BearerToken;
import com.comic.server.annotation.CurrentUser;
import com.comic.server.config.OpenApiConfig;
import com.comic.server.feature.auth.dto.JwtResponse;
import com.comic.server.feature.user.dto.NewPasswordRequest;
import com.comic.server.feature.user.dto.UpdateUserProfileRequest;
import com.comic.server.feature.user.dto.UserProfile;
import com.comic.server.feature.user.model.User;
import com.comic.server.feature.user.repository.UserRepository;
import com.comic.server.feature.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User")
public record UserController(UserService userService, UserRepository userRepository) {

  @Operation(
      summary = "Get the user profile",
      description = "Get the user profile",
      security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_NAME))
  @GetMapping("/profile")
  public ResponseEntity<UserProfile> getUserProfile(@CurrentUser User user) {
    return ResponseEntity.ok(userService.getUserProfile(user));
  }

  @Operation(
      summary = "Update user profile",
      description = "Update the user profile",
      security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_NAME))
  @PatchMapping(path = "/profile", consumes = "application/merge-patch+json")
  public ResponseEntity<UserProfile> updateUserProfile(
      @CurrentUser User user, @RequestBody UpdateUserProfileRequest request) {
    return ResponseEntity.ok(userService.updateUserProfile(user, request));
  }

  @Operation(
      summary = "Change the password for an account",
      description =
          """
Change the password for an account
**Required**:
- Refresh token is passed in the header as a Bearer token
- Just use if an user is logged in and want to change password

**Note**
- The new password must be different from the old password

""",
      security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_NAME))
  @PostMapping("/new-password")
  public JwtResponse changePassword(
      @BearerToken String refreshToken, @RequestBody NewPasswordRequest newPasswordRequest) {
    return userService.changePassword(refreshToken, newPasswordRequest);
  }
}
