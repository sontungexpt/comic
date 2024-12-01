package com.comic.server.feature.user.service;

import com.comic.server.feature.auth.dto.JwtResponse;
import com.comic.server.feature.user.dto.NewPasswordRequest;
import com.comic.server.feature.user.dto.UpdateUserProfileRequest;
import com.comic.server.feature.user.dto.UserProfile;
import com.comic.server.feature.user.model.User;

public interface UserService {

  boolean existsByUsername(String username);

  User createUser(String username, String password, String name);

  User updateUser(User user);

  UserProfile getUserProfile(User user);

  UserProfile updateUserProfile(User user, UpdateUserProfileRequest request);

  User findUserByPubId(String userPubId);

  JwtResponse changePassword(String refreshToken, NewPasswordRequest newPasswordRequest);
}
