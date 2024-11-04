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
import com.comic.server.feature.user.model.User;
import com.comic.server.feature.user.service.FollowedComicService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.ErrorCategory;
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
import java.util.stream.Collectors;
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

  public static final String BASE_URL = "https://otruyenapi.com/v1/api";

  private ObjectMapper objectMapper = new ObjectMapper();
  private final ComicRepository comicRepository;
  private final ComicCategoryRepository comicCategoryRepository;
  private final ThirdPartyMetadataRepository thirdPartyMetadataRepository;
  private final OtruyenComicAdapter otruyenComicAdapter;
  private final OtruyenComicCategoryAdapter otruyenComicCategoryAdapter;
  private final MongoTemplate mongoTemplate;
  private final FollowedComicService followedComicService;

  private Optional<OtruyenComic> getOtruyenComicBySlug(String slug) {
    var client = HttpClient.newHttpClient();

    var request =
        HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/truyen-tranh/" + slug))
            .header("Accept", "application/json")
            .GET()
            .build();

    log.info("Sending request to " + request.uri());
    return client
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(HttpResponse::body)
        .thenApply(
            body -> {
              JsonNode map = null;
              try {
                map = objectMapper.readTree(body);
              } catch (JsonProcessingException e) {
                log.error("Error when getting comic from " + SourceName.OTRUYEN);
                e.printStackTrace();
              }

              OtruyenComic comic = null;
              if (map != null) {
                comic =
                    objectMapper.convertValue((map.get("data")).get("item"), OtruyenComic.class);
              }

              return Optional.ofNullable(comic);
            })
        .join();
  }

  @Override
  public ComicDetailDTO getComicDetail(
      String comicId, SourceName sourceName, Pageable pageable, User user) {
    if (sourceName == SourceName.OTRUYEN) {
      Comic comic =
          comicRepository
              .findByIdAndThirdPartySourceName(comicId, SourceName.OTRUYEN)
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          Comic.class,
                          Map.of("id", comicId, "thirdPartySource.name", SourceName.OTRUYEN)));

      String otruyenSlug = comic.getThirdPartySource().getSlug();

      log.info("Getting comic detail from " + SourceName.OTRUYEN);
      var comicDetail =
          otruyenComicAdapter.convertToComicDetailDTO(
              getOtruyenComicBySlug(otruyenSlug)
                  .orElseThrow(
                      () -> new ResourceNotFoundException(OtruyenComic.class, "slug", otruyenSlug)),
              comicId,
              pageable);

      if (user != null) {
        comicDetail.setFollowed(followedComicService.isUserFollowingComic(user.getId(), comicId));
      }

      return comicDetail;
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
      log.info("Save new comics from {} to database ", SourceName.OTRUYEN);
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
                comicDTOs.add(new ComicDTO(comic, comicsResult.get(index).getCategories()));
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

    var remainingPages = comicPaginationMetadata.getRemainingPages();

    final long MAX_REQUEST = remainingPages > 2 ? 2 : 1;

    var client = HttpClient.newHttpClient();

    List<CompletableFuture<HttpResponse<String>>> futures = new ArrayList<>((int) MAX_REQUEST);

    for (int i = 0; i < MAX_REQUEST; i++) {
      var request =
          HttpRequest.newBuilder()
              .uri(URI.create(BASE_URL + "/danh-sach/truyen-moi?page=" + nextPage + i))
              .header("Accept", "application/json")
              .GET()
              .build();
      log.info("Sending request to " + request.uri());
      futures.add(client.sendAsync(request, HttpResponse.BodyHandlers.ofString()));
    }

    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
        .thenApply(
            v -> {
              List<ComicDTO> comicDTOs = new ArrayList<>();
              long totalComicsForPageImpl = 0;
              for (int i = 0; i < MAX_REQUEST; i++) {
                try {
                  var future = futures.get(i);
                  var response = future.get();
                  JsonNode map = objectMapper.readTree(response.body());
                  var data = map.get("data");

                  List<ComicDTO> comicDTOs_i = proccessComics(data);

                  if (!comicDTOs_i.isEmpty()) {
                    comicDTOs.addAll(comicDTOs_i);
                    metadata.incrementTotalSyncedItems(comicDTOs_i.size());

                    JsonNode pagination = data.get("params").get("pagination");
                    long totalComics = pagination.get("totalItems").asLong();
                    long itemsPerPage = pagination.get("totalItemsPerPage").asLong();
                    totalComicsForPageImpl =
                        (totalComicsForPageImpl > 0 ? totalComicsForPageImpl : totalComics)
                            - metadata.getTotalSyncedItems();

                    comicPaginationMetadata.setTotalComics(totalComics);
                    comicPaginationMetadata.setTotalItemsPerPage(itemsPerPage);
                    comicPaginationMetadata.setCurrentSyncedPage(nextPage + i);

                    log.info(
                        "Updated metadata:"
                            + " totalComics = {},"
                            + " totalItemsPerPage = {},"
                            + " currentSyncedPage = {}",
                        totalComics,
                        itemsPerPage,
                        nextPage + i);
                  }
                } catch (Exception e) {
                  log.error("Error processing response", e);
                }
              }

              log.info(
                  "Saving updated metadata to repository with source name" + SourceName.OTRUYEN);
              thirdPartyMetadataRepository.save(metadata);

              return new PageImpl<>(
                  comicDTOs.stream()
                      .skip(pageable.getOffset())
                      .limit(pageable.getPageSize())
                      .toList(),
                  pageable,
                  totalComicsForPageImpl);
            })
        .exceptionally(
            e -> {
              e.printStackTrace();
              return new PageImpl<ComicDTO>(List.of(), pageable, 0);
            })
        .join();
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
            .toList()
            .stream()
            .limit(8) // limit request to 8
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

                  log.info("Sending request to " + request.uri());
                  return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                })
            .filter(future -> future != null)
            .toList();

    log.info("Sent {} requests to fetch comics data contains categories id", futures.size());

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
                                Set<String> comicCategorySlugs =
                                    comicDTO.getCategories().stream()
                                        .map((c) -> c.getSlug())
                                        .collect(Collectors.toSet());

                                return comicCategorySlugs.containsAll(otruyenCategoriesSlug);
                              })
                          .toList();
                  int comicDTOsListSize = comicDTOs.size();

                  return new PageImpl<ComicDTO>(
                      new ArrayList<>(comicDTOsList),
                      PageRequest.ofSize(comicDTOsListSize > 0 ? comicDTOsListSize : 1),
                      comicDTOsListSize);
                })
            .exceptionally(
                e -> {
                  log.error("Error processing response", e);
                  return new PageImpl<ComicDTO>(List.of(), PageRequest.ofSize(1), 0);
                })
            .join();

    log.info("Saving updated metadata to repository with source name" + SourceName.OTRUYEN);
    thirdPartyMetadataRepository.save(metadata);

    log.info(
        "Completed getComicsWithCategories filter by categories id with {} comics",
        result.getContent().size());

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

    log.info("Sending request to " + request.uri());

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
              } catch (JsonProcessingException e) {
                e.printStackTrace();
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
