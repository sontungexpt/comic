package com.comic.server.feature.comic.model;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class Translator extends Person {

  public Translator() {
    super();
  }

  public Translator(String name) {
    super(name);
  }

  public Translator(String name, String desc, String imageUrl) {
    super(name, desc, imageUrl);
  }
}
