package com.comic.server.common.model;

public interface Sluggable {
  String createSlugFrom();

  String getSlug();

  void setSlug(String slug);
}
