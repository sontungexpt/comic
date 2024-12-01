package com.comic.server.feature.user.service.impl;

import com.comic.server.feature.user.model.User;
import com.comic.server.feature.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public record UserDetailsPasswordServiceImpl(
    UserRepository userRepository, PasswordEncoder passwordEncoder)
    implements UserDetailsPasswordService {

  @Override
  public UserDetails updatePassword(UserDetails userDetails, String newPassword) {
    assert userDetails instanceof User;
    User user = (User) userDetails;
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
    return user;
  }
}
