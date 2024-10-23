package com.comic.server.feature.comic.service.impl;

import com.comic.server.exceptions.ResourceNotFoundException;
import com.comic.server.feature.comic.dto.ComicDTO;
import com.comic.server.feature.comic.dto.ComicDetailDTO;
import com.comic.server.feature.comic.dto.otruyen.OtruyenChapterShortInfo;
import com.comic.server.feature.comic.dto.otruyen.OtruyenComic;
import com.comic.server.feature.comic.dto.otruyen.OtruyenComicAdapter;
import com.comic.server.feature.comic.dto.otruyen.OtruyenComicAdapter.ComicWithCategories;
import com.comic.server.feature.comic.dto.otruyen.OtruyenComicChapterAdapter;
import com.comic.server.feature.comic.model.Comic;
import com.comic.server.feature.comic.model.chapter.ShortInfoChapter;
import com.comic.server.feature.comic.model.thirdparty.OtruyenMetadata;
import com.comic.server.feature.comic.model.thirdparty.SourceName;
import com.comic.server.feature.comic.repository.ComicRepository;
import com.comic.server.feature.comic.repository.ThirdPartyMetadataRepository;
import com.comic.server.feature.comic.service.GetComicService;
import com.comic.server.utils.ConsoleUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtruyenComicServiceImpl implements GetComicService {

  private String BASE_URL = "https://otruyenapi.com/v1/api";
  private String CDN_IMAGE_URL = "https://img.otruyenapi.com/uploads";
  private final ObjectMapper objectMapper;
  private final ComicRepository comicRepository;
  private final ThirdPartyMetadataRepository thirdPartyMetadataRepository;
  private final OtruyenComicAdapter otruyenComicAdapter;
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
              .findByIdAndOriginalSourceName(comicId, SourceName.OTRUYEN)
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          Comic.class,
                          Map.of("id", comicId, "originalSource.name", SourceName.OTRUYEN)));

      return otruyenComicAdapter.convertToComicDetailDTO(
          getOtruyenComicBySlug(comic.getOriginalSource().getSlug())
              .orElseThrow(
                  () -> new ResourceNotFoundException(OtruyenComic.class, "slug", comicId)),
          comicId,
          pageable);
    }
    return null;
  }

  @Override
  public Page<ComicDTO> getComicsWithCategories(Pageable pageable, List<String> filterCategoryIds) {
    OtruyenMetadata metadata =
        (OtruyenMetadata)
            thirdPartyMetadataRepository
                .findBySourceName(SourceName.OTRUYEN)
                .orElseGet(() -> new OtruyenMetadata());

    var nextPage = metadata.getNextPage();

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

    try {
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      JsonNode map = objectMapper.readTree(response.body());

      var data = map.get("data");

      List<OtruyenComic> items =
          objectMapper.convertValue(data.get("items"), new TypeReference<List<OtruyenComic>>() {});

      if (!items.isEmpty()) {
        List<ComicWithCategories> comicsResult = new ArrayList<>();
        List<Comic> comics = new ArrayList<>();

        for (OtruyenComic comic : items) {
          ComicWithCategories comicWithCategories =
              otruyenComicAdapter.convertToComic(comic, false);
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
                    comic.setId(result.getId().asObjectId().getValue().toHexString());
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
                            .originalSource(comic.getOriginalSource())
                            .build());
                  });
        } catch (DuplicateKeyException e) {
        }

        JsonNode pagination = data.get("params").get("pagination");
        long totalComics = pagination.get("totalItems").asLong();
        long itemsPerPage = pagination.get("totalItemsPerPage").asLong();
        long totalComicsForPageImpl = totalComics - itemsPerPage * metadata.getCurrentSyncedPage();

        metadata.setTotalComics(totalComics);
        metadata.setTotalItemsPerPage(itemsPerPage);
        metadata.setCurrentSyncedPage(nextPage);

        thirdPartyMetadataRepository.save(metadata);
        log.info(
            "Updated metadata: totalComics = {}, totalItemsPerPage = {}, currentSyncedPage = {}",
            totalComics,
            itemsPerPage,
            nextPage);

        return new PageImpl<>(
            comicDTOs.stream().skip(pageable.getOffset()).limit(pageable.getPageSize()).toList(),
            pageable,
            totalComicsForPageImpl);
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    return Page.empty();
  }

  // @Override
  // public CompletableFuture<Page<ComicDTO>> getComicsWithCategories(
  //     Pageable pageable, List<String> filterCategoryIds) {
  //   OtruyenMetadata metadata =
  //       (OtruyenMetadata)
  //           thirdPartyMetadataRepository
  //               .findBySourceName(SourceName.OTRUYEN)
  //               .orElseGet(() -> new OtruyenMetadata());

  //   var nextPage = metadata.getNextPage();

  //   if (nextPage < 0) {
  //     log.info("No more data to sync in" + SourceName.OTRUYEN);
  //     return CompletableFuture.completedFuture(Page.empty());
  //   }

  //   var client = HttpClient.newHttpClient();
  //   var request =
  //       HttpRequest.newBuilder()
  //           .uri(URI.create(BASE_URL + "/danh-sach/truyen-moi?page=" + nextPage))
  //           .header("Accept", "application/json")
  //           .GET()
  //           .build();

  //   log.info("Sending request to fetch comics from otruyen api source: {}", nextPage);
  //   return client
  //       .sendAsync(request, HttpResponse.BodyHandlers.ofString())
  //       .thenApply(
  //           response -> {
  //             log.info("Received response for page {}: Status {}", nextPage,
  // response.statusCode());
  //             try {
  //               return objectMapper.readTree(response.body());
  //             } catch (IOException e) {
  //               log.error("Error parsing response body", e);
  //               throw new CompletionException(e);
  //             }
  //           })
  //       .thenApply(
  //           body -> {
  //             var data = body.get("data");
  //             return processComics(data, metadata, pageable);
  //           })
  //       .exceptionally(
  //           ex -> {
  //             log.error("Error occurred while fetching comics: ", ex);
  //             return Page.empty();
  //           });
  // }

  // private Page<ComicDTO> processComics(JsonNode data, OtruyenMetadata metadata, Pageable
  // pageable) {

  //   List<OtruyenComic> items =
  //       objectMapper.convertValue(data.get("items"), new TypeReference<List<OtruyenComic>>() {});

  //   if (!items.isEmpty()) {

  //     List<ComicWithCategories> comicsResult = new ArrayList<>();
  //     List<Comic> comics = new ArrayList<>();

  //     for (OtruyenComic comic : items) {
  //       ComicWithCategories comicWithCategories = otruyenComicAdapter.convertToComic(comic,
  // false);
  //       comicsResult.add(comicWithCategories);
  //       comics.add(comicWithCategories.getComic());
  //     }

  //     List<ComicDTO> comicDTOs = new ArrayList<>();

  //     try {
  //       var bulkOperation = mongoTemplate.bulkOps(BulkMode.UNORDERED, Comic.class);
  //       bulkOperation.insert(comics);
  //       var results = bulkOperation.execute();
  //       results
  //           .getInserts()
  //           .forEach(
  //               result -> {
  //                 int index = result.getIndex();
  //                 Comic comic = comics.get(index);
  //                 comic.setId(result.getId().asObjectId().getValue().toHexString());
  //                 comicDTOs.add(
  //                     ComicDTO.builder()
  //                         .id(comic.getId())
  //                         .name(comic.getName())
  //                         .authors(comic.getAuthors())
  //                         .artists(comic.getArtists())
  //                         .categories(comicsResult.get(index).getCategories())
  //                         .tags(comic.getTags())
  //                         .characters(comic.getCharacters())
  //                         .newChapters(comic.getNewChaptersInfo())
  //                         .status(comic.getStatus())
  //                         .alternativeNames(comic.getAlternativeNames())
  //                         .summary(comic.getSummary())
  //                         .thumbnailUrl(comic.getThumbnailUrl())
  //                         .originalSource(comic.getOriginalSource())
  //                         .build());
  //               });
  //     } catch (DuplicateKeyException e) {
  //       log.info("This comic is already in the database");
  //     }
  //     updateMetadata(data, metadata);

  //     return new PageImpl<>(
  //         comicDTOs.stream().skip(pageable.getOffset()).limit(pageable.getPageSize()).toList(),
  //         pageable,
  //         metadata.getTotalComics());
  //   }
  //   return Page.empty();
  // }

  // private void updateMetadata(JsonNode data, OtruyenMetadata metadata) {

  //   JsonNode pagination = data.get("params").get("pagination");
  //   long totalComics = pagination.get("totalItems").asLong();
  //   long itemsPerPage = pagination.get("totalItemsPerPage").asLong();

  //   metadata.setTotalComics(totalComics);
  //   metadata.setTotalItemsPerPage(itemsPerPage);
  //   metadata.setCurrentSyncedPage(metadata.getNextPage());
  //   thirdPartyMetadataRepository.save(metadata);

  //   log.info(
  //       "Updated metadata: totalComics = {}, totalItemsPerPage = {}, currentSyncedPage = {}",
  //       totalComics,
  //       itemsPerPage,
  //       metadata.getNextPage());
  // }

  @Override
  public List<ShortInfoChapter> getChaptersByComicId(String comicId) {

    Comic comic =
        comicRepository
            .findByIdAndOriginalSourceName(comicId, SourceName.OTRUYEN)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        Comic.class,
                        Map.of("id", comicId, "originalSource.name", SourceName.OTRUYEN)));

    var souceComic =
        getOtruyenComicBySlug(comic.getOriginalSource().getSlug())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        OtruyenComic.class, "slug", comic.getOriginalSource().getSlug()));

    var serverData = souceComic.getServerDatas();
    ConsoleUtils.prettyPrint(serverData);

    if (serverData.isEmpty()) {
      return List.of();
    }

    List<OtruyenChapterShortInfo> chapterShortInfos = serverData.get(0).getChapters();

    return chapterShortInfos.stream()
        .map(
            chapter ->
                OtruyenComicChapterAdapter.convertToShortInfoChapter(
                    chapter, new ObjectId(comicId)))
        .toList();
  }

  @Override
  public long countComics() {
    var result =
        ((OtruyenMetadata)
            thirdPartyMetadataRepository.findBySourceName(SourceName.OTRUYEN).orElse(null));

    if (result == null) {
      return 0;
    }

    return result.getTotalComics();
  }
}
