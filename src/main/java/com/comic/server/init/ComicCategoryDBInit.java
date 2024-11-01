package com.comic.server.init;

import com.comic.server.feature.comic.model.ComicCategory;
import com.comic.server.feature.comic.service.ComicCategoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public record ComicCategoryDBInit(
    ObjectMapper objectMapper, ComicCategoryService comicCategoryService)
    implements CommandLineRunner {

  @Override
  public void run(String... args) throws Exception {
    if (comicCategoryService.countCategories() > 0) {
      return;
    }

    try {

      String json = Files.readString(Paths.get("src/main/resources/static/raws/category.json"));
      comicCategoryService.createComicCategories(convert(json));
    } catch (Exception e) {
      throw new RuntimeException("Error when create comic categories", e);
    }
  }

  public List<ComicCategory> convert(String jsonString) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    List<ComicCategory> categories = new ArrayList<>();

    // Parse the JSON string to an array of JSON objects

    JsonNode jsonObjectList = objectMapper.readTree(jsonString);

    for (int i = 0; i < jsonObjectList.size(); i++) {
      JsonNode jsonObject = jsonObjectList.get(i);
      String name = jsonObject.get("name").asText();
      JsonNode descriptionNode = jsonObject.get("description");
      String description = descriptionNode != null ? descriptionNode.asText() : "";
      ComicCategory category = new ComicCategory(name, description);
      categories.add(category);
    }

    return categories;
  }
}
