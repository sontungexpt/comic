package com.comic.server.utils;

import com.comic.server.annotation.JsonPatchIgnore;
import com.comic.server.annotation.JsonPatchIgnoreProperties;
import com.comic.server.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Lazy
@Service
@RequiredArgsConstructor
public class JsonMergePatchUtils {

  private final MongoTemplate mongoTemplate;

  private ObjectMapper mapper = RawJsonConvertor.getMapper();

  /**
   * Applies a JSON patch to the given object while ignoring specified fields.
   *
   * @param origin The original object to be patched.
   * @param patchRequest The JSON patch request containing the changes.
   * @param ignoreFieldPaths Field paths to be excluded from patching.
   * @param <E> The type of the object being patched.
   * @return The updated object after applying the patch.
   * @throws IllegalStateException If an error occurs while applying the patch.
   * @throws JsonPatchException If an error occurs during patch application.
   */
  @SuppressWarnings("unchecked")
  public <E> E apply(E origin, JsonNode patchRequest, String... ignoreFieldPaths) {
    if (ignoreFieldPaths != null && ignoreFieldPaths.length > 0) {
      patchRequest = JsonNodeUtils.removeFields(patchRequest, ignoreFieldPaths);
    }
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

  /**
   * Applies a JSON patch to the given object, excluding fields marked with {@link JsonPatchIgnore}
   * or defined in {@link JsonPatchIgnoreProperties}.
   *
   * @param origin The original object to be patched.
   * @param patchRequest The JSON patch request containing the changes.
   * @param <E> The type of the object being patched.
   * @return The updated object after applying the patch.
   * @throws IllegalStateException If an error occurs while applying the patch.
   * @throws JsonPatchException If an error occurs during patch application.
   */
  public <E> E apply(E origin, JsonNode patchRequest) {
    Class<?> clazz = origin.getClass();
    return apply(
        origin,
        removeJsonPatchIgnoreFields(patchRequest, clazz),
        getJsonPatchIgnoreProperties(clazz));
  }

  /**
   * Retrieves an entity from the MongoDB database by its ID. If the entity is not found, a {@link
   * <p>ResourceNotFoundException} is thrown.
   *
   * @param <E> The type of the entity to retrieve.
   * @param id The ID of the entity to retrieve.
   * @param entityClass The class of the entity to retrieve.
   */
  private <E> E getEntity(Object id, Class<E> entityClass) {
    E entity = mongoTemplate.findById(id, entityClass);
    if (entity == null) {
      throw new ResourceNotFoundException(entityClass, "id", id);
    }
    return entity;
  }

  /**
   * Patches an existing entity in the MongoDB database using the provided patch request. This
   * method retrieves the entity by its ID, applies the patch, and then saves the updated entity.
   *
   * @param <ID> The type of the entity ID (e.g., String, Long).
   * @param <E> The type of the entity being patched.
   * @param id The ID of the entity to patch.
   * @param entityClass The class of the entity to patch.
   * @param patchRequest The JSON patch request containing the changes.
   * @return The updated entity after applying the patch.
   * @throws ResourceNotFoundException If the entity with the given ID is not found in the database.
   */
  public <E> E patch(Object id, Class<E> entityClass, JsonNode patchRequest) {
    return mongoTemplate.save(apply(getEntity(id, entityClass), patchRequest));
  }

  /**
   * Patches an existing entity in the MongoDB database using the provided patch request, while
   * allowing specific fields to be ignored during the patching process. This method retrieves the
   * entity by its ID, applies the patch (excluding ignored fields), and then saves the updated
   * entity.
   *
   * @param <ID> The type of the entity ID (e.g., String, Long).
   * @param <E> The type of the entity being patched.
   * @param id The ID of the entity to patch.
   * @param entityClass The class of the entity to patch.
   * @param patchRequest The JSON patch request containing the changes.
   * @param ignoreFieldPaths The paths of fields to ignore during the patching.
   * @return The updated entity after applying the patch with the ignored fields.
   * @throws ResourceNotFoundException If the entity with the given ID is not found in the database.
   */
  public <ID, E> E patch(
      ID id, Class<E> entityClass, JsonNode patchRequest, String... ignoreFieldPaths) {
    return mongoTemplate.save(apply(getEntity(id, entityClass), patchRequest, ignoreFieldPaths));
  }

  /**
   * Patches an existing entity in the MongoDB database using the provided patch request. This
   * method retrieves the entity by its ID, applies the patch, and then saves the updated entity.
   *
   * @param <E> The type of the entity being patched.
   * @param id The ID of the entity to patch.
   * @param entityClass The class of the entity to patch.
   * @param patchRequest The JSON patch request containing the changes.
   * @return The updated entity after applying the patch.
   * @throws ResourceNotFoundException If the entity with the given ID is not found in the database.
   */
  public <T> T patch(T obj, JsonNode patchRequest) {
    return mongoTemplate.save(apply(obj, patchRequest));
  }

  /**
   * Removes fields from the JSON node that are annotated with {@link JsonPatchIgnore}. This method
   * will recursively check nested fields to ensure all ignored fields are excluded from the patch.
   *
   * @param node The JSON node representing the patch request.
   * @param clazz The class of the original object being patched.
   * @return A new JSON node with the ignored fields removed.
   */
  public JsonNode removeJsonPatchIgnoreFields(JsonNode node, Class<?> clazz) {
    List<String> removedFields = new ArrayList<>();
    node.fields()
        .forEachRemaining(
            entry -> {
              String fieldName = entry.getKey();
              Field field = ReflectionUtils.getDeclaredField(clazz, fieldName);
              if (field != null) {
                JsonPatchIgnore jsonPatchIgnore = field.getAnnotation(JsonPatchIgnore.class);
                if (jsonPatchIgnore != null) {
                  removedFields.add(fieldName);
                } else {
                  JsonNode fieldNode = entry.getValue();
                  Class<?> fieldType = field.getType();
                  if (!ReflectionUtils.isPrimitiveTypeOrString(field) && fieldNode.isObject()) {
                    removeJsonPatchIgnoreFields(fieldNode, fieldType);
                  } else if (fieldNode.isArray() && fieldType.isArray()) {
                    fieldNode.forEach(
                        item -> {
                          if (item.isObject()) {
                            removeJsonPatchIgnoreFields(item, fieldType);
                          }
                        });
                  }
                }
              }
            });

    if (node instanceof ObjectNode) {
      removedFields.forEach(((ObjectNode) node)::remove);
    }
    return node;
  }

  /**
   * Removes fields from the JSON node that are listed in the {@link JsonPatchIgnoreProperties}
   * annotation on the class. This method checks if the class has the annotation and removes the
   * specified fields.
   *
   * @param node The JSON node representing the patch request.
   * @param clazz The class of the original object being patched.
   * @return A new JSON node with the fields to be ignored removed.
   */
  public JsonNode removeJsonPatchIgnorePropertiesFields(JsonNode node, Class<?> clazz) {
    JsonPatchIgnoreProperties jsonPatchIgnoreProperties =
        clazz.getAnnotation(JsonPatchIgnoreProperties.class);
    if (jsonPatchIgnoreProperties != null) {
      return JsonNodeUtils.removeFields(node, jsonPatchIgnoreProperties.value());
    }
    return node;
  }

  /**
   * Get the fields to be ignored from the {@link JsonPatchIgnoreProperties} annotation on the
   *
   * @param clazz The class of the original object being patched.
   * @return An array of field names to be ignored.
   */
  private String[] getJsonPatchIgnoreProperties(Class<?> clazz) {
    JsonPatchIgnoreProperties jsonPatchIgnoreProperties =
        clazz.getAnnotation(JsonPatchIgnoreProperties.class);
    return jsonPatchIgnoreProperties != null ? jsonPatchIgnoreProperties.value() : new String[] {};
  }
}
