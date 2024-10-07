package com.comic.server.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr353.JSR353Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class BeanConfig {

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();

    // SimpleModule module = new SimpleModule();
    // module.addDeserializer(Slice.class, new SliceDeserializer());
    // module.addSerializer(Slice.class, new SliceSerializer());
    // mapper.registerModule(module);

    return mapper
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        // https://stackoverflow.com/questions/45662820/how-to-set-format-of-string-for-java-time-instant-using-objectmapper
        .registerModule(new JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

        // https://cassiomolin.com/programming/using-http-patch-in-spring/
        .registerModule(new JSR353Module());
  }
}
