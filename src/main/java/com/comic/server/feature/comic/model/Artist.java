package com.comic.server.feature.comic.model;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class Artist extends Person {
  public Artist() {
    super();
  }

  public Artist(String name) {
    super(name);
  }

  public Artist(String name, String desc, String imageUrl) {
    // super(name, desc, imageUrl, PersonType.ARTIST);
    super(name, desc, imageUrl);
  }
}
