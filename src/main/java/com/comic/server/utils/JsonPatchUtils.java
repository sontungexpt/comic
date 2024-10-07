package com.comic.server.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import java.io.IOException;
import org.springframework.stereotype.Service;

@Service
public record JsonPatchUtils(ObjectMapper mapper) {

  @SuppressWarnings("unchecked")
  public <E> E applyPatch(E origin, JsonNode patchRequest) {

    try {
      JsonNode entityNode = mapper.convertValue(origin, JsonNode.class);
      JsonPatch jsonPatch = JsonPatch.fromJson(patchRequest);
      JsonNode updatedJsonNode = jsonPatch.apply(entityNode);
      // JsonNode updatedJsonNode = mapper.readerForUpdating(entityNode).readValue(patchRequest);
      return (E) mapper.treeToValue(updatedJsonNode, origin.getClass());
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to apply patch", ex);
    } catch (JsonPatchException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public <E> E applyMergePatch(E origin, JsonNode patchRequest) {
    try {
      JsonNode entityNode = mapper.convertValue(origin, JsonNode.class);
      JsonMergePatch jsonMergePatch = JsonMergePatch.fromJson(patchRequest);
      JsonNode updatedJsonNode = jsonMergePatch.apply(entityNode);
      return (E) mapper.treeToValue(updatedJsonNode, origin.getClass());
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to apply patch", ex);
    } catch (JsonPatchException e) {
      throw new RuntimeException(e);
    }
  }
}
