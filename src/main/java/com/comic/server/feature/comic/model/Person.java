package com.comic.server.feature.comic.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public abstract class Person {

  @NotBlank private String name;

  private String description;

  private String imageUrl;
}
