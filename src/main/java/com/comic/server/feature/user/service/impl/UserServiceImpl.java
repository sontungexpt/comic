package com.comic.server.feature.user.service.impl;

import com.comic.server.exceptions.ResourceNotFoundException;
import com.comic.server.feature.auth.dto.JwtResponse;
import com.comic.server.feature.auth.model.RefreshToken;
import com.comic.server.feature.auth.service.RefreshTokenService;
import com.comic.server.feature.comic.repository.ComicRepository;
import com.comic.server.feature.user.dto.NewPasswordRequest;
import com.comic.server.feature.user.dto.UpdateUserProfileRequest;
import com.comic.server.feature.user.dto.UserProfile;
import com.comic.server.feature.user.model.User;
import com.comic.server.feature.user.repository.UserRepository;
import com.comic.server.feature.user.service.UserService;
import com.comic.server.utils.JsonMergePatchUtils;
import org.bson.types.ObjectId;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public record UserServiceImpl(
    JsonMergePatchUtils jsonMergePatchUtils,
    UserRepository userRepository,
    UserDetailsPasswordService userDetailsPasswordService,
    ComicRepository comicRepository,
    RefreshTokenService refreshTokenService,
    PasswordEncoder passwordEncoder)
    implements UserService {

  @Override
  public boolean existsByUsername(String phoneNumber) {
    return userRepository.existsByUsername(phoneNumber);
  }

  @Override
  @Transactional
  public User createUser(String username, String password, String name) {

    User user =
        User.builder()
            .username(username)
            .password(passwordEncoder.encode(password))
            .name(name)
            .build();
    return userRepository.save(user);
  }

  @Override
  public UserProfile getUserProfile(User user) {
    var profile = UserProfile.from(user);
    profile.setTotalCreatedComics(comicRepository.countByOwnerId(new ObjectId(user.getId())));
    return profile;
  }

  @Override
  @Transactional
  public UserProfile updateUserProfile(User user, UpdateUserProfileRequest request) {
    if (request.getName() != null) {
      user.setName(request.getName());
    }
    return UserProfile.from(userRepository.save(user));
  }

  @Override
  public JwtResponse changePassword(String refreshToken, NewPasswordRequest newPasswordRequest) {
    RefreshToken savedRefreshToken =
        refreshTokenService.getAndValidateRefreshToken(refreshToken, this::handleInstruction);
    User user = findUserByPubId(savedRefreshToken.getUserPubId());
    if (!passwordEncoder.matches(newPasswordRequest.getOldPassword(), user.getPassword())) {
      throw new IllegalArgumentException("Old password is incorrect");
    }
    userDetailsPasswordService.updatePassword(user, newPasswordRequest.getNewPassword());
    return refreshTokenService.refreshJwtTokens(savedRefreshToken);
  }

  @Override
  public User findUserByPubId(String pubId) {
    return userRepository
        .findByPubId(pubId)
        .orElseThrow(() -> new ResourceNotFoundException(User.class, "pubId", pubId));
  }

  @Override
  public User updateUser(User user) {
    return userRepository.save(user);
  }

  public void handleInstruction(RefreshToken refreshToken) {
    System.out.println("Change the password for an account");
    System.out.println(
        "Change the password for an account\n\n**Usecase**:\n- UC_account-doi-mat-khau\n\n");
  }
}
