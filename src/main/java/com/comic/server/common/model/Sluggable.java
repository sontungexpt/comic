package com.comic.server.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.lang.reflect.Field;

/** Interface to support objects that can generate a slug from a string attribute. */
public interface Sluggable {

  /**
   * Generates a URL-friendly slug from the object's string attribute.
   *
   * <p>This method converts a string (e.g., name or title) into a slug format by removing invalid
   * characters, converting to lowercase, and replacing spaces with hyphens.
   *
   * @return A URL-friendly slug string.
   */
  String generateSlug();

  /**
   * Retrieves the current slug of the object, if it exists.
   *
   * @return The current slug of the object.
   */
  @JsonIgnore
  default String getSlugFieldName() {
    return "slug";
  }

  /**
   * Retrieves the current slug of the object, if it exists.
   *
   * @return The current slug of the object.
   */
  default String getSlug() {
    try {
      Field slugField = getClass().getField(getSlugFieldName());
      return (String) slugField.get(this);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      return null;
    } catch (Exception e) {
      return null;
    }
  }
}
