package com.comic.server.common.payload;

public interface Sluggable {
  String createSlugFrom();

  String getSlug();

  void setSlug(String slug);
}
