package com.comic.server.feature.user.model.authorization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpMethod;

@Getter
@Setter
@Document(collection = "endpoints")
@JsonIgnoreProperties(
    value = {"id"},
    allowGetters = true)
public class ApiEndpoint {

  @Id private String id;

  @NotNull private String endpoint;

  @NotNull private HttpMethod method;

  private String displayName;

  private String description;

  @Override
  public int hashCode() {
    return endpoint.hashCode() + method.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    else if (!(obj instanceof ApiEndpoint)) return false;
    return endpoint.equals(((ApiEndpoint) obj).endpoint)
        && method.equals(((ApiEndpoint) obj).method);
  }
}
