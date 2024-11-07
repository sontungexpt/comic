package com.comic.server.common.model;


/**
 * Interface to support objects that can generate a slug from a string attribute. When an object
 * implements this interface, the {@link SluggableModelListener} will automatically generate a slug
 * for the object based on the fields annotated with {@link AutoSlugify}.
 */
public interface Sluggable<ID> {

  ID getId();
}
