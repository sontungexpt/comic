package com.comic.server.init;

import com.comic.server.feature.comic.model.Comic;
import com.comic.server.feature.comic.model.ComicCategory;
import com.comic.server.feature.comic.model.Source;
import com.comic.server.feature.comic.repository.ComicCategoryRepository;
import com.comic.server.feature.comic.repository.ComicRepository;
import com.comic.server.feature.comic.service.ComicService;
import com.comic.server.utils.ConsoleUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public record ComicDBInit(
    ObjectMapper objectMapper,
    ComicService comicService,
    ComicRepository comicRepository,
    ComicCategoryRepository comicCategoryRepository)
    implements CommandLineRunner {

  @Override
  public void run(String... args) throws Exception {
    if (comicService.countComics() > 0) {
      return;
    }

    try {
      String json = Files.readString(Paths.get("src/main/resources/static/raws/comic.json"));
      List<Comic> comics = convert(json);

      comicService.createComics(comics);

    } catch (Exception e) {
      ConsoleUtils.prettyPrint(e);
      throw new RuntimeException("Error when create assetlinks");
    }
  }

  public List<Comic> convert(String jsonString) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();

    List<Object> jsonObjectList = objectMapper.readValue(jsonString, List.class);
    List<Comic> comics = new ArrayList<>();

    // // Iterate through each JSON object and map it to a ComicCategory object
    for (Object jsonObject : jsonObjectList) {
      Map<String, Object> jsonMap = objectMapper.convertValue(jsonObject, Map.class);
      String name = (String) jsonMap.get("name");
      List originalNames = objectMapper.convertValue(jsonMap.get("origin_name"), List.class);

      String id = (String) jsonMap.get("_id");
      String slug = (String) jsonMap.get("slug");

      String status = (String) jsonMap.get("status");
      Comic.Status comicStatus;

      if (status.equals("ongoing")) {
        comicStatus = Comic.Status.ONGOING;
      } else if (status.equals("new")) {
        comicStatus = Comic.Status.NEW;
      } else if (status.equals("coming_soon")) {
        comicStatus = Comic.Status.COMMING_SOON;
      } else {
        comicStatus = Comic.Status.COMPLETED;
      }
      List categories = objectMapper.convertValue(jsonMap.get("category"), List.class);
      List<ObjectId> categoryIds = new ArrayList<>();

      for (Object category : categories) {
        Map<String, Object> categoryMap = objectMapper.convertValue(category, Map.class);
        String categoryName = (String) categoryMap.get("name");
        ComicCategory comicCategory = comicCategoryRepository.findByName(categoryName).orElse(null);
        if (comicCategory != null) {
          categoryIds.add(new ObjectId(comicCategory.getId()));
        }
      }

      Comic comic =
          Comic.builder()
              .name(name)
              .originalNames(originalNames)
              .status(comicStatus)
              .categoryIds(categoryIds)
              .thumbnailUrl(
                  "https://otruyenapi.com/uploads/comics/" + (String) jsonMap.get("thumb_url"))
              .originalSource(
                  Source.builder()
                      .idFromSource(id)
                      .slugFromSource(slug)
                      .name("otruyen")
                      .baseUrl("https://otruyenapi.com")
                      .build())
              .build();

      comics.add(comic);
      log.info("Imported comic: {}", comic);
    }

    return comics;
  }
}
