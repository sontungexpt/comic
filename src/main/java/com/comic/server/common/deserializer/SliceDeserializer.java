package com.comic.server.common.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

public class SliceDeserializer extends JsonDeserializer<Slice> {

  @Override
  public Slice deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    JsonNode node = p.getCodec().readTree(p);
    List<?> content = p.getCodec().treeToValue(node.get("content"), List.class);
    Pageable pageable = p.getCodec().treeToValue(node.get("pageable"), PageRequest.class);
    boolean hasNext = node.get("hasNext").asBoolean();
    return new SliceImpl<>(content, pageable, hasNext);
  }
}
