package com.comic.server.init;

import com.comic.server.feature.comic.model.ComicCategory;
import com.comic.server.feature.comic.service.ComicCategoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ComicCategoryDBInit implements CommandLineRunner {

  private final ComicCategoryService comicCategoryService;
  private final String CATEGORY_JSON_PATH = "src/main/resources/static/raws/category.json";

  @Override
  public void run(String... args) throws Exception {
    if (comicCategoryService.countCategories() > 0) return;

    try {
      String json = Files.readString(Paths.get(CATEGORY_JSON_PATH));
      comicCategoryService.createComicCategories(readJson(json));
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    }
  }

  public List<ComicCategory> readJson(String jsonString) throws JsonProcessingException {
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
