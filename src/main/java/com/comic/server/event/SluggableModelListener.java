package com.comic.server.event;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.comic.server.common.payload.Sluggable;
import com.github.slugify.Slugify;
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
    String creatorName = sluggable.createSlugFrom();
    if (StringUtils.hasText(creatorName) && !StringUtils.hasText(sluggable.getSlug())) {
      final Slugify slg = Slugify.builder().build();
      sluggable.setSlug(slg.slugify(creatorName + "-" + NanoIdUtils.randomNanoId()));
    }
    Document document = event.getDocument();
    if (document != null) {
      document.put("slug", sluggable.getSlug());
    }
  }
}
