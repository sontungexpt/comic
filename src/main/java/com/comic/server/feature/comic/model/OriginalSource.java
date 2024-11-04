package com.comic.server.feature.comic.model;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class OriginalSource implements Source {

  private String name;

  private String description;

  private String link;

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getLink() {
    return link;
  }

  public OriginalSource(String name) {
    this.name = name;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, link);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    OriginalSource source = (OriginalSource) o;

    return name.equals(source.name) && link.equals(source.link);
  }
}
