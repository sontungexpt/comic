package com.comic.server.feature.user.model.authorization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "permissions")
@JsonIgnoreProperties(
    value = {"id"},
    allowGetters = true)
public class Permission {

  @Id private String id;

  @NotNull private Permission name;

  private String displayName;

  private String description;

  private Set<ApiEndpoint> apiEndpoints;

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    else if (!(obj instanceof Permission)) return false;
    return name.equals(((Permission) obj).name);
  }
}
