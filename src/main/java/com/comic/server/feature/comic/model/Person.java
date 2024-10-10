package com.comic.server.feature.comic.model;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public abstract class Person {

  private String name;

  private String description;

  private String imageUrl;
}
