package com.comic.server.init;

import com.comic.server.feature.comic.model.ComicCategory;
import com.comic.server.feature.comic.service.ComicCategoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

      // ConsoleUtils.prettyPrint(list);

      // return new ObjectMapper().readValue(json, Object.class);
    } catch (Exception e) {
      throw new RuntimeException("Error when create assetlinks");
    }
  }

  public List<ComicCategory> convert(String jsonString) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    List<ComicCategory> categories = new ArrayList<>();

    // Parse the JSON string to an array of JSON objects
    List<Object> jsonObjectList = objectMapper.readValue(jsonString, List.class);

    // Iterate through each JSON object and map it to a ComicCategory object
    for (Object jsonObject : jsonObjectList) {
      Map<String, Object> jsonMap = objectMapper.convertValue(jsonObject, Map.class);
      String name = (String) jsonMap.get("name");
      categories.add(new ComicCategory(name));
    }

    return categories;
  }
}
