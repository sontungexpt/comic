package com.comic.server.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;

public class ConsoleUtils {

  private static ObjectMapper objectMapper = new ObjectMapper();

  static {
    objectMapper.registerModule(new JavaTimeModule());

    // https://stackoverflow.com/questions/45662820/how-to-set-format-of-string-for-java-time-instant-using-objectmapper
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }

  @SneakyThrows
  public static void prettyPrint(Object object) {
    if (object == null) {
      System.out.println("No object to log: null");
      return;
    }

    System.out.println(
        object.getClass().getSimpleName()
            + ": "
            + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object));
  }
}
