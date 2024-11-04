package com.comic.server.feature.comic.model;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class Character extends Person {

  public Character() {
    super();
  }

  public Character(String name) {
    super(name);
  }

  public Character(String name, String desc, String imageUrl) {
    super(name, desc, imageUrl);
  }
}
