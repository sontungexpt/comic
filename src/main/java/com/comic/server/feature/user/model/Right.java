package com.comic.server.feature.user.model;

import com.comic.server.feature.user.model.authorization.Role;
import java.security.Permission;
import java.util.Set;

public class Right {

  private Set<Role> role;

  private Set<Permission> permission;
}
