package com.comic.server.feature.user.model;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.comic.server.annotation.JsonPatchIgnore;
import com.comic.server.feature.user.enums.RoleType;
import com.comic.server.feature.user.enums.UserStatus;
import com.comic.server.validation.annotation.OptimizedName;
import com.comic.server.validation.annotation.Password;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@SuperBuilder
@Schema(description = "User model", name = "User")
@Document(collection = "users")
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@JsonIgnoreProperties(
    value = {"id", "pubId", "authorities"},
    allowGetters = true)
public class User implements UserDetails, Persistable<String> {
  @JsonPatchIgnore @JsonIgnore @Id private String id;

  // Why we need this?
  // The public id is a unique identifier for the account.
  // It is used to identify the account in the system.
  // It can be shared to anyone and it is not sensitive information.
  // Why don't use phoneNumber as the public id?
  // Because the phoneNumber can be changed by the user,
  // and we need a unique identifier for the account.
  @Default
  @Indexed(unique = true)
  @Schema(description = "The public id of the account")
  @JsonPatchIgnore
  private String pubId = NanoIdUtils.randomNanoId();

  @Indexed(unique = true)
  @Schema(description = "The phone number of the account", requiredMode = RequiredMode.REQUIRED)
  @JsonPatchIgnore
  private String username;

  @JsonIgnore
  @Password
  @Schema(description = "The password of the account", requiredMode = RequiredMode.REQUIRED)
  @JsonPatchIgnore
  private String password;

  @OptimizedName
  @Schema(description = "The name of the account", requiredMode = RequiredMode.REQUIRED)
  private String name;

  private String email;

  @Schema(description = "The avatar path of the account")
  private String avatar;

  @Schema(description = "The introduction of the user")
  private String bio;

  @Default
  @Schema(description = "The status of the account")
  @JsonPatchIgnore
  private UserStatus status = UserStatus.ACTIVE;

  @Schema(description = "The roles of the account")
  @Default
  @JsonPatchIgnore
  private Set<RoleType> roles = Set.of(RoleType.READER, RoleType.POSTER);

  @CreatedDate
  @Schema(description = "The created time of the account")
  @JsonIgnore
  @JsonPatchIgnore
  private Instant createdAt;

  @LastModifiedDate
  @Schema(description = "The updated time of the account")
  @JsonIgnore
  @JsonPatchIgnore
  private Instant updatedAt;

  @JsonPatchIgnore @JsonIgnore @Transient
  private Collection<? extends GrantedAuthority> authorities;

  public boolean hasRole(RoleType roleType) {
    return roles.stream().anyMatch(r -> r.equals(roleType));
  }

  public User(User user) {
    this.id = user.getId();
    this.pubId = user.getPubId();
    this.username = user.getUsername();
    this.password = user.getPassword();
    this.name = user.getName();
    this.status = user.getStatus();
    this.roles = user.getRoles();
    this.createdAt = user.getCreatedAt();
    this.updatedAt = user.getUpdatedAt();
    this.authorities = user.getAuthorities();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    if (roles == null) {
      return Collections.emptySet();
    } else {
      if (authorities == null || authorities.isEmpty()) {
        Collection<SimpleGrantedAuthority> auths = new HashSet<>();
        roles.forEach(
            role -> {
              auths.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
            });
        authorities = auths;
      }
      return authorities;
    }
  }

  @Override
  @JsonIgnore
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonExpired() {
    return status != UserStatus.ARCHIVED && status != UserStatus.DELETED;
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonLocked() {
    return status != UserStatus.BANNED;
  }

  @Override
  @JsonIgnore
  public boolean isCredentialsNonExpired() {
    return status != UserStatus.COMPROMISED;
  }

  @JsonIgnore
  @Override
  public boolean isEnabled() {
    return status == UserStatus.ACTIVE;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    else if (obj instanceof User) {
      User that = (User) obj;
      return id.equals(that.getId())
          || username.equals(that.getUsername())
          || pubId.equals(that.getPubId());
    }
    return false;
  }

  @Override
  @JsonIgnore
  public boolean isNew() {
    return createdAt == null || id == null;
  }

  // @Override
  // public Map<String, Object> getAttributes() {
  //   return Map.of("id", id, "pubId", pubId, "username", username, "name", name, "roles", roles);
  // }
}
