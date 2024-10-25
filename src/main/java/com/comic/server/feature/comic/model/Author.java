package com.comic.server.feature.comic.model;


import lombok.experimental.SuperBuilder;

@SuperBuilder
public class Author extends Person {

  public Author() {
    super();
  }

  public Author(String name) {
    super(name);
  }

  public Author(String name, String desc, String imageUrl) {
    super(name, desc, imageUrl);
  }
}
