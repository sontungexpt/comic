package com.comic.server.feature.user.service;

import com.comic.server.feature.user.model.User;

public interface UserService {

  boolean existsByUsername(String username);

  User createUser(String username, String password, String name);
}
