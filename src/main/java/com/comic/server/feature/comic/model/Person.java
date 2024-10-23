package com.comic.server.feature.comic.model;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
public abstract class Person implements Serializable {

  @NotBlank private String name;

  private String description;

  private String imageUrl;

  public Person(String name) {
    this.name = name;
  }
}
