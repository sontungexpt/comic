package com.comic.server.common.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.springframework.data.domain.Slice;

public class SliceSerializer extends JsonSerializer<Slice> {

  @Override
  public void serialize(Slice slice, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {

    gen.writeStartObject();
    gen.writeObjectField("content", slice.getContent());
    gen.writeObjectField("pageable", slice.getPageable());
    gen.writeBooleanField("hasNext", slice.hasNext());
    gen.writeEndObject();
  }
}
