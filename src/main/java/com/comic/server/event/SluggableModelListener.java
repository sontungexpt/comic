package com.comic.server.event;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.comic.server.annotation.AutoSlugify;
import com.comic.server.annotation.AutoSlugify.Separator;
import com.comic.server.annotation.AutoSlugify.UpdateStrategy;
import com.comic.server.common.model.Sluggable;
import com.github.slugify.Slugify;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

@SuppressWarnings("rawtypes")
@Component
@RequiredArgsConstructor
@Slf4j
public class SluggableModelListener extends AbstractMongoEventListener<Sluggable> {

  private final MongoTemplate mongoTemplate;

  private int determineNanoIdSize(int length) {
    if (length > 100) return 5;
    else if (length > 80) return 7;
    else if (length > 70) return 9;
    else if (length > 60) return 11;
    else if (length > 40) return 13;
    else if (length > 30) return 15;
    else if (length > 20) return 17;
    else if (length > 15) return 19;
    return NanoIdUtils.DEFAULT_SIZE;
  }

  private String createSlug(
      String value, boolean unique, boolean fromUniqueField, Separator separator) {
    if (!StringUtils.hasText(value)) {
      throw new IllegalArgumentException("Cannot generate a slug from an empty string.");
    }

    final Slugify slg =
        Slugify.builder()
            .transliterator(true)
            .underscoreSeparator(separator == Separator.UNDERSCORE)
            .build();

    String slug = slg.slugify(value);
    if (unique && !fromUniqueField) {
      slug += separator.getValue();
      int value_len = value.length();
      int nanoid_size = determineNanoIdSize(value_len);
      slug +=
          NanoIdUtils.randomNanoId(
              NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, nanoid_size);
    }

    return slug;
  }

  @Override
  public void onBeforeSave(BeforeSaveEvent<Sluggable> event) {
    var sluggable = event.getSource();
    Class<?> slugableClazz = sluggable.getClass();

    Sluggable oldSlugable = null;

    for (Field field : slugableClazz.getDeclaredFields()) {
      field.setAccessible(true);
      AutoSlugify autoSlugify = field.getAnnotation(AutoSlugify.class);
      if (autoSlugify != null && field.getType().equals(String.class)) {
        UpdateStrategy strategy = autoSlugify.updateStrategy();

        boolean haveToUpdate =
            ReflectionUtils.getField(field, sluggable) == null
                || sluggable.getId() == null
                || strategy == UpdateStrategy.ON_DOCUMENT_SAVE;

        if (!haveToUpdate) {
          if (strategy == UpdateStrategy.NEVER_UPDATE) {
            continue;
          } else if (strategy == UpdateStrategy.ON_VALUE_CHANGE) {
            if (oldSlugable == null) {
              oldSlugable = (Sluggable) mongoTemplate.findById(sluggable.getId(), slugableClazz);
              if (oldSlugable == null) {
                haveToUpdate = true;
              }
            }
          }
        }

        List<String> values = new ArrayList<>();

        for (String f : autoSlugify.fields()) {
          Field foundField = ReflectionUtils.findField(slugableClazz, f);
          if (foundField == null) {
            throw new IllegalArgumentException(
                "Cannot find field "
                    + f
                    + " in class "
                    + slugableClazz.getName()
                    + "when slugifying for "
                    + field.getName());
          }

          foundField.setAccessible(true);

          Object value = ReflectionUtils.getField(foundField, sluggable);

          if (haveToUpdate == false) {
            Object oldValue = ReflectionUtils.getField(foundField, oldSlugable);
            if (value != null && oldValue != null && !oldValue.equals(value)) {
              haveToUpdate = true;
            }
          }
          if (value != null) {
            values.add(value.toString().trim());
          }
        }

        if (!haveToUpdate) {
          continue;
        }

        log.info(
            "Updating slug for field {} in class {} in document {}",
            field.getName(),
            slugableClazz,
            sluggable.getId());

        Separator sep = autoSlugify.separator();
        String slug =
            createSlug(
                String.join(sep.getValue(), values),
                autoSlugify.unique(),
                autoSlugify.fromUniqueField(),
                sep);

        ReflectionUtils.setField(field, sluggable, slug);
        Document document = event.getDocument();
        if (document != null) {
          document.put(field.getName(), slug);
        }
      }
    }
  }
}
