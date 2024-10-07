package com.comic.server.common.serializer;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import org.springframework.data.mongodb.core.convert.LazyLoadingProxy;

public class DBRefSerializer extends JsonSerializer<Object> {

  @Override
  public void serialize(Object value, JsonGenerator generator, SerializerProvider provider)
      throws JsonGenerationException, IOException {

    provider.defaultSerializeValue(value, generator);
  }

  @Override
  public void serializeWithType(
      Object value, JsonGenerator generator, SerializerProvider provider, TypeSerializer typeSer)
      throws IOException {

    Object target = value;
    if (value instanceof LazyLoadingProxy) {
      LazyLoadingProxy proxy = (LazyLoadingProxy) value;
      target = proxy.getTarget();
      provider.defaultSerializeValue(target, generator);
    } else {
      provider.defaultSerializeValue(target, generator);
    }
  }
}
