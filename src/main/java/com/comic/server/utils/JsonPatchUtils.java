package com.comic.server.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import java.io.IOException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class JsonPatchUtils {

  // private MongoTemplate mongoTemplate;
  @SuppressWarnings("unchecked")
  public <E> E applyPatch(E origin, JsonNode patchRequest, String... ignoreFieldPaths) {
    ObjectMapper mapper = RawJsonConvertor.getMapper();
    try {
      JsonNode entityNode = mapper.convertValue(origin, JsonNode.class);
      JsonPatch jsonPatch = JsonPatch.fromJson(patchRequest);
      JsonNode updatedJsonNode = jsonPatch.apply(entityNode);
      return (E) mapper.treeToValue(updatedJsonNode, origin.getClass());
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to apply patch", ex);
    } catch (JsonPatchException e) {
      throw new RuntimeException(e);
    }
  }
}
