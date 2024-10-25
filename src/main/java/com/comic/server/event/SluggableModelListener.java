package com.comic.server.event;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.comic.server.common.model.Sluggable;
import com.github.slugify.Slugify;
import java.util.Locale;
import org.bson.Document;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SluggableModelListener extends AbstractMongoEventListener<Sluggable> {

  @Override
  public void onBeforeSave(BeforeSaveEvent<Sluggable> event) {
    Sluggable sluggable = event.getSource();
    String slug = sluggable.getSlug();

    if (!StringUtils.hasText(slug)) {
      String sourceString = sluggable.generateSlug();
      if (StringUtils.hasText(sourceString)) {
        final Slugify slg = Slugify.builder().locale(Locale.ENGLISH).build();
        if (sluggable.isCreatedFromUniqueString()) {
          slug = slg.slugify(sourceString);
        } else {
          slug = slg.slugify(sourceString) + "-" + NanoIdUtils.randomNanoId();
        }
      } else {
        throw new IllegalArgumentException("Slug is required");
      }
    }
    Document document = event.getDocument();
    if (document != null) {
      document.put(sluggable.getSlugFieldName(), slug);
    }
  }
}
