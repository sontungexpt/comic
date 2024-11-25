package com.comic.server.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr353.JSR353Module;

public class RawJsonConvertor {
  private static ObjectMapper mapper;

  public static final ObjectMapper getMapper() {
    if (mapper == null) {
      mapper = new ObjectMapper();
      mapper
          // https://stackoverflow.com/questions/45662820/how-to-set-format-of-string-for-java-time-instant-using-objectmapper
          .registerModule(new JavaTimeModule())
          .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

          // https://cassiomolin.com/programming/using-http-patch-in-spring/
          .registerModule(new JSR353Module())
          .setAnnotationIntrospector(
              new JacksonAnnotationIntrospector() {

                @Override
                public Boolean isIgnorableType(AnnotatedClass ac) {
                  return null;
                }

                @Override // since 2.12
                public JsonIgnoreProperties.Value findPropertyIgnoralByName(
                    MapperConfig<?> config, Annotated a) {
                  return JsonIgnoreProperties.Value.empty();
                }

                @Override
                public boolean hasIgnoreMarker(AnnotatedMember m) {
                  return false;
                }
              });
    }
    return mapper;
  }

  public static <E> E convertValue(JsonNode jsonNode, Class<E> clazz) {
    try {
      return getMapper().treeToValue(jsonNode, clazz);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
    try {
      return getMapper().convertValue(fromValue, toValueType);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public <T> T convertValue(Object fromValue, TypeReference<T> toValueType) {
    try {
      return getMapper().convertValue(fromValue, toValueType);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
