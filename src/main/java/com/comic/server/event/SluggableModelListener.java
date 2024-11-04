package com.comic.server.event;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.comic.server.annotation.AutoSlugify;
import com.comic.server.common.model.Sluggable;
import com.github.slugify.Slugify;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import org.bson.Document;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

@Component
public class SluggableModelListener extends AbstractMongoEventListener<Sluggable> {

  @Override
  public void onBeforeSave(BeforeSaveEvent<Sluggable> event) {
    Sluggable sluggable = event.getSource();
    Class<?> slugableClazz = sluggable.getClass();

    for (Field field : slugableClazz.getDeclaredFields()) {
      field.setAccessible(true);
      if (field.getType().equals(String.class)) {

        AutoSlugify autoSlugify = field.getAnnotation(AutoSlugify.class);
        if (autoSlugify != null) {
          Boolean underscoreSep = autoSlugify.underscoreSeperation();
          boolean unique = autoSlugify.unique();
          boolean fromUniqueField = autoSlugify.fromUniqueField();

          final Slugify slg = Slugify.builder().underscoreSeparator(underscoreSep).build();

          List<String> values =
              Arrays.stream(autoSlugify.fields())
                  .map(
                      f -> {
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
                        return ReflectionUtils.getField(foundField, sluggable).toString();
                      })
                  .toList();

          String sep = underscoreSep ? "_" : "-";
          String concatFieldsValue = String.join(sep, values);

          if (StringUtils.hasText(concatFieldsValue)) {
            String slug = slg.slugify(concatFieldsValue);
            if (unique && !fromUniqueField) {
              slug += sep;
              int value_len = concatFieldsValue.length();

              int nanoid_size = NanoIdUtils.DEFAULT_SIZE;
              if (value_len > 100) {
                nanoid_size = 6;
              } else if (value_len > 80) {
                nanoid_size = 8;
              } else if (value_len > 60) {
                nanoid_size = 10;
              } else if (value_len > 40) {
                nanoid_size = 12;
              } else if (value_len > 30) {
                nanoid_size = 14;
              } else if (value_len > 20) {
                nanoid_size = 16;
              } else if (value_len > 15) {
                nanoid_size = 18;
              }
              slug +=
                  NanoIdUtils.randomNanoId(
                      NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
                      NanoIdUtils.DEFAULT_ALPHABET,
                      nanoid_size);
            }

            ReflectionUtils.setField(field, sluggable, slug);

            Document document = event.getDocument();
            if (document != null) {
              document.put(field.getName(), slug);
            }
          } else {
            throw new IllegalArgumentException("Cannot generate a slug from an empty string.");
          }
        }
      }
    }
  }
}
