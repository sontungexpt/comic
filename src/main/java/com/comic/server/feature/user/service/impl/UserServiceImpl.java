package com.comic.server.feature.user.service.impl;

import com.comic.server.feature.user.dto.UpdateUserProfileRequest;
import com.comic.server.feature.user.dto.UserProfile;
import com.comic.server.feature.user.model.User;
import com.comic.server.feature.user.repository.UserRepository;
import com.comic.server.feature.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public record UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder)
    implements UserService {

  @Override
  public boolean existsByUsername(String phoneNumber) {
    return userRepository.existsByUsername(phoneNumber);
  }

  @Override
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
    return UserProfile.from(user);
  }

  @Override
  public UserProfile updateUserProfile(User user, UpdateUserProfileRequest request) {
    return null;

    // JsonNode jsonNode = RawCon.convertValue(userProfile, JsonNode.class);

  }
}
