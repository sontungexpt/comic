package com.comic.server.feature.user.controller;

import com.comic.server.feature.user.repository.UserRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
// @RolesAllowed(RoleType.Fields.CUSTOMER)
public record UserController(UserRepository userRepository) {}
