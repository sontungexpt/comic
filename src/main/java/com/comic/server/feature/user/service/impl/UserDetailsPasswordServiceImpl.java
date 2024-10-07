package com.comic.server.feature.user.service.impl;

import com.comic.server.feature.user.model.User;
import com.comic.server.feature.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.stereotype.Service;

@Service
public record UserDetailsPasswordServiceImpl(UserRepository userRepository)
    implements UserDetailsPasswordService {

  @Override
  public UserDetails updatePassword(UserDetails userDetails, String newPassword) {
    assert userDetails instanceof User;

    User user = (User) userDetails;
    user.setPassword(newPassword);
    userRepository.save(user);
    return user;
  }
}
