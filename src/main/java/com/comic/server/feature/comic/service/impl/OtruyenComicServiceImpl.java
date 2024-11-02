package com.comic.server.feature.comic.service.impl;

import com.comic.server.exceptions.ResourceNotFoundException;
import com.comic.server.feature.comic.dto.ComicDTO;
import com.comic.server.feature.comic.dto.ComicDetailDTO;
import com.comic.server.feature.comic.dto.otruyen.OtruyenChapterShortInfo;
import com.comic.server.feature.comic.dto.otruyen.OtruyenComic;
import com.comic.server.feature.comic.dto.otruyen.OtruyenComicAdapter;
import com.comic.server.feature.comic.dto.otruyen.OtruyenComicAdapter.ComicWithCategories;
import com.comic.server.feature.comic.dto.otruyen.OtruyenComicCategoryAdapter;
import com.comic.server.feature.comic.dto.otruyen.OtruyenComicChapterAdapter;
import com.comic.server.feature.comic.model.Comic;
import com.comic.server.feature.comic.model.chapter.ShortInfoChapter;
import com.comic.server.feature.comic.model.thirdparty.OtruyenMetadata;
import com.comic.server.feature.comic.model.thirdparty.SourceName;
import com.comic.server.feature.comic.repository.ComicCategoryRepository;
import com.comic.server.feature.comic.repository.ComicRepository;
import com.comic.server.feature.comic.repository.ThirdPartyMetadataRepository;
import com.comic.server.feature.comic.service.ChainGetComicService;
import com.comic.server.utils.ConsoleUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.ErrorCategory;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.BulkOperationException;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtruyenComicServiceImpl implements ChainGetComicService {

  private String BASE_URL = "https://otruyenapi.com/v1/api";
  private String CDN_IMAGE_URL = "https://img.otruyenapi.com/uploads";
  private ObjectMapper objectMapper = new ObjectMapper();
  private final ComicRepository comicRepository;
  private final ComicCategoryRepository comicCategoryRepository;
  private final ThirdPartyMetadataRepository thirdPartyMetadataRepository;
  private final OtruyenComicAdapter otruyenComicAdapter;
  private final OtruyenComicCategoryAdapter otruyenComicCategoryAdapter;
  private final MongoTemplate mongoTemplate;

  private Optional<OtruyenComic> getOtruyenComicBySlug(String slug) {
    try {
      var client = HttpClient.newHttpClient();

      var request =
          HttpRequest.newBuilder()
              .uri(URI.create(BASE_URL + "/truyen-tranh/" + slug))
              .header("Accept", "application/json")
              .GET()
              .build();

      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      JsonNode map = objectMapper.readTree(response.body());

      OtruyenComic comic =
          objectMapper.convertValue((map.get("data")).get("item"), OtruyenComic.class);

      comic.setThumbUrl(CDN_IMAGE_URL + "/comics/" + comic.getThumbUrl());

      return Optional.of(comic);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  @Override
  public ComicDetailDTO getComicDetail(String comicId, SourceName sourceName, Pageable pageable) {
    if (sourceName == SourceName.OTRUYEN) {
      Comic comic =
          comicRepository
              .findByIdAndThirdPartySourceName(comicId, SourceName.OTRUYEN)
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          Comic.class,
                          Map.of("id", comicId, "thirdPartySource.name", SourceName.OTRUYEN)));

      return otruyenComicAdapter.convertToComicDetailDTO(
          getOtruyenComicBySlug(comic.getThirdPartySource().getSlug())
              .orElseThrow(
                  () -> new ResourceNotFoundException(OtruyenComic.class, "slug", comicId)),
          comicId,
          pageable);
    }
    return null;
  }

  private List<ComicDTO> proccessComics(JsonNode data) {
    List<OtruyenComic> items =
        objectMapper.convertValue(data.get("items"), new TypeReference<List<OtruyenComic>>() {});

    if (items.isEmpty()) {
      return List.of();
    }

    List<ComicWithCategories> comicsResult = new ArrayList<>();
    List<Comic> comics = new ArrayList<>();

    for (OtruyenComic comic : items) {
      ComicWithCategories comicWithCategories = otruyenComicAdapter.convertToComic(comic, true);
      comicsResult.add(comicWithCategories);
      comics.add(comicWithCategories.getComic());
    }

    List<ComicDTO> comicDTOs = new ArrayList<>();

    try {
      var bulkOperation = mongoTemplate.bulkOps(BulkMode.UNORDERED, Comic.class);
      bulkOperation.insert(comics);
      var results = bulkOperation.execute();

      results
          .getInserts()
          .forEach(
              result -> {
                int index = result.getIndex();
                Comic comic = comics.get(index);
                // comic.setId(result.getId().asObjectId().getValue().toHexString());
                comicDTOs.add(
                    ComicDTO.builder()
                        .id(comic.getId())
                        .name(comic.getName())
                        .authors(comic.getAuthors())
                        .artists(comic.getArtists())
                        .categories(comicsResult.get(index).getCategories())
                        .tags(comic.getTags())
                        .characters(comic.getCharacters())
                        .newChapters(comic.getNewChaptersInfo())
                        .status(comic.getStatus())
                        .alternativeNames(comic.getAlternativeNames())
                        .summary(comic.getSummary())
                        .thumbnailUrl(comic.getThumbnailUrl())
                        .thirdPartySource(comic.getThirdPartySource())
                        .build());
              });
    } catch (BulkOperationException e) {
      for (var error : e.getErrors()) {
        if (error.getCategory() != ErrorCategory.DUPLICATE_KEY) {
          throw e;
        }
      }
    } catch (DuplicateKeyException e) {
    }
    return comicDTOs;
  }

  private Page<ComicDTO> getComicsWithCategories(Pageable pageable) {
    OtruyenMetadata metadata =
        (OtruyenMetadata)
            thirdPartyMetadataRepository
                .findBySourceName(SourceName.OTRUYEN)
                .orElseGet(() -> new OtruyenMetadata());

    var comicPaginationMetadata = metadata.getComicPagination();
    var nextPage = comicPaginationMetadata.getNextPage();

    if (nextPage < 0) {
      log.info("No more data to sync in" + SourceName.OTRUYEN);
      return Page.empty();
    }

    var client = HttpClient.newHttpClient();
    var request =
        HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/danh-sach/truyen-moi?page=" + nextPage))
            .header("Accept", "application/json")
            .GET()
            .build();

    var pageResult =
        client
            .sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenApply(
                body -> {
                  JsonNode map = null;
                  try {
                    map = objectMapper.readTree(body);
                  } catch (Exception e) {
                    log.error("Error parsing response body", e);
                    throw new RuntimeException(e);
                  }

                  if (map == null) {
                    return new PageImpl<ComicDTO>(List.of(), pageable, 0);
                  }
                  var data = map.get("data");

                  List<ComicDTO> comicDTOs = proccessComics(data);

                  long comicDTOsSize = comicDTOs.size();

                  if (comicDTOsSize > 0) {
                    metadata.incrementTotalSyncedItems(comicDTOsSize);

                    JsonNode pagination = data.get("params").get("pagination");
                    long totalComics = pagination.get("totalItems").asLong();
                    long itemsPerPage = pagination.get("totalItemsPerPage").asLong();
                    long totalComicsForPageImpl = totalComics - metadata.getTotalSyncedItems();

                    comicPaginationMetadata.setTotalComics(totalComics);
                    comicPaginationMetadata.setTotalItemsPerPage(itemsPerPage);
                    comicPaginationMetadata.setCurrentSyncedPage(nextPage);

                    thirdPartyMetadataRepository.save(metadata);
                    log.info(
                        "Updated metadata: totalComics = {}, totalItemsPerPage = {},"
                            + " currentSyncedPage = {}",
                        totalComics,
                        itemsPerPage,
                        nextPage);

                    return new PageImpl<>(
                        comicDTOs.stream()
                            .skip(pageable.getOffset())
                            .limit(pageable.getPageSize())
                            .toList(),
                        pageable,
                        totalComicsForPageImpl);
                  }
                  return new PageImpl<ComicDTO>(List.of(), pageable, 0);
                })
            .join();

    return pageResult;
  }

  private Page<ComicDTO> getComicsWithCategories(List<String> filterCategoryIds) {
    OtruyenMetadata metadata =
        (OtruyenMetadata)
            thirdPartyMetadataRepository
                .findBySourceName(SourceName.OTRUYEN)
                .orElseGet(() -> new OtruyenMetadata());

    List<String> otruyenCategoriesSlug =
        comicCategoryRepository.findAllById(filterCategoryIds).stream()
            .map(otruyenComicCategoryAdapter::getSlugFromCategory)
            .toList();

    List<String> otruyenCategoriesSlugValid =
        otruyenCategoriesSlug.stream()
            .filter(slug -> metadata.getCategoryPagination(slug).getNextPage() > 0)
            .toList();

    var client = HttpClient.newHttpClient();

    List<CompletableFuture<HttpResponse<String>>> futures =
        otruyenCategoriesSlugValid.stream()
            .map(
                slug -> {
                  var categoryPaginationMetadata = metadata.getCategoryPagination(slug);

                  var nextPage = categoryPaginationMetadata.getNextPage();

                  var request =
                      HttpRequest.newBuilder()
                          .uri(URI.create(BASE_URL + "/the-loai/" + slug + "?page=" + nextPage))
                          .header("Accept", "application/json")
                          .GET()
                          .build();
                  return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                })
            .filter(future -> future != null)
            .toList();

    var result =
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(
                v -> {
                  Set<ComicDTO> comicDTOs = new HashSet<>();

                  for (int i = 0; i < futures.size(); i++) {
                    try {
                      var future = futures.get(i);
                      var response = future.get();
                      JsonNode map = objectMapper.readTree(response.body());
                      var data = map.get("data");
                      ConsoleUtils.prettyPrint(map);

                      List<ComicDTO> comicDTOs_i = proccessComics(data);
                      metadata.incrementTotalSyncedItems(comicDTOs_i.size());

                      if (!comicDTOs_i.isEmpty()) {
                        comicDTOs.addAll(comicDTOs_i);

                        String slug = otruyenCategoriesSlugValid.get(i);
                        var categoryPaginationMetadata = metadata.getCategoryPagination(slug);
                        JsonNode pagination = data.get("params").get("pagination");
                        long totalComics = pagination.get("totalItems").asLong();
                        long itemsPerPage = pagination.get("totalItemsPerPage").asLong();

                        categoryPaginationMetadata.setTotalComics(totalComics);
                        categoryPaginationMetadata.setTotalItemsPerPage(itemsPerPage);
                        categoryPaginationMetadata.setCurrentSyncedPage(
                            categoryPaginationMetadata.getNextPage());

                        // metadata.setCategoryPagination(slug, categoryPaginationMetadata);
                      }

                    } catch (Exception e) {
                      log.error("Error processing response", e);
                    }
                  }

                  List<ComicDTO> comicDTOsList =
                      comicDTOs.stream()
                          .filter(
                              comicDTO -> {
                                var categories = comicDTO.getCategories();
                                return otruyenCategoriesSlug.stream()
                                    .allMatch(
                                        slug ->
                                            categories.stream()
                                                .anyMatch((c) -> c.getSlug().equals(slug)));
                              })
                          .toList();
                  int comicDTOsListSize = comicDTOs.size();

                  ConsoleUtils.prettyPrint(metadata);

                  return new PageImpl<ComicDTO>(
                      new ArrayList<>(comicDTOsList),
                      PageRequest.ofSize(comicDTOsListSize > 0 ? comicDTOsListSize : 1),
                      comicDTOsListSize);
                })
            .join();

    thirdPartyMetadataRepository.save(metadata);

    return result;
  }

  @Override
  public Page<ComicDTO> getComicsWithCategories(Pageable pageable, List<String> filterCategoryIds) {
    if (filterCategoryIds == null || filterCategoryIds.isEmpty()) {
      return getComicsWithCategories(pageable);
    }

    Page<ComicDTO> page = getComicsWithCategories(filterCategoryIds);
    return new PageImpl<>(
        page.getContent().stream()
            .skip(pageable.getOffset())
            .limit(pageable.getPageSize())
            .toList(),
        pageable,
        page.getTotalElements());
  }

  @Override
  public List<ShortInfoChapter> getChaptersByComicId(String comicId) {

    Comic comic =
        comicRepository
            .findByIdAndThirdPartySourceName(comicId, SourceName.OTRUYEN)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        Comic.class,
                        Map.of("id", comicId, "thirdPartySource.name", SourceName.OTRUYEN)));

    return getChaptersByComic(comic);
  }

  public List<ShortInfoChapter> getChaptersByComic(Comic comic) {

    var souceComic =
        getOtruyenComicBySlug(comic.getThirdPartySource().getSlug())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        OtruyenComic.class, "slug", comic.getThirdPartySource().getSlug()));

    var serverData = souceComic.getServerDatas();

    if (serverData.isEmpty()) {
      return List.of();
    }

    List<OtruyenChapterShortInfo> chapterShortInfos = serverData.get(0).getChapters();

    return chapterShortInfos.stream()
        .map(
            chapter ->
                OtruyenComicChapterAdapter.convertToShortInfoChapter(
                    chapter, new ObjectId(comic.getId())))
        .toList();
  }

  @Override
  public long countComics(List<String> filterCategoryIds) {
    if (filterCategoryIds != null && !filterCategoryIds.isEmpty()) {
      return 0;
    }

    var result =
        ((OtruyenMetadata)
            thirdPartyMetadataRepository.findBySourceName(SourceName.OTRUYEN).orElse(null));

    if (result == null) {
      return 0;
    }

    return result.getComicPagination().getTotalComics();
  }

  @Override
  public Page<ComicDTO> searchComic(String keyword, Pageable pageable) {

    var client = HttpClient.newHttpClient();
    var request =
        HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/tim-kiem?keyword=" + keyword + "&page=1"))
            .header("Accept", "application/json")
            .GET()
            .build();

    return client
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(HttpResponse::body)
        .thenApply(
            body -> {
              try {

                JsonNode map = objectMapper.readTree(body);
                var data = map.get("data");
                List<ComicDTO> comicDTOs = proccessComics(data);

                if (!comicDTOs.isEmpty()) {
                  OtruyenMetadata metadata =
                      (OtruyenMetadata)
                          thirdPartyMetadataRepository
                              .findBySourceName(SourceName.OTRUYEN)
                              .orElseGet(() -> new OtruyenMetadata());
                  metadata.incrementTotalSyncedItems(comicDTOs.size());
                  thirdPartyMetadataRepository.save(metadata);
                }

                return new PageImpl<>(comicDTOs, pageable, comicDTOs.size());
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .join();
  }

  @Override
  public ChainGetComicService getNextService() {
    return null;
  }
}
