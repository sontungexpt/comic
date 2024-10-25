package com.comic.server.feature.comic.service.impl;

import com.comic.server.feature.comic.dto.otruyen.OtruyenChapterDetail;
import com.comic.server.feature.comic.dto.otruyen.OtruyenComicChapterAdapter;
import com.comic.server.feature.comic.model.chapter.AbstractChapter;
import com.comic.server.feature.comic.service.ChapterChainService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtruyenChapterServiceImpl implements ChapterChainService {

  private String CHAPTER_API_BASE_URL = "https://sv1.otruyencdn.com/v1/api/chapter";
  // private final ComicService nextService;
  private final ObjectMapper objectMapper;

  @Override
  public ChapterChainService getNextService() {
    return null;
  }

  @Override
  public AbstractChapter getChapterDetailById(String comicId, String chapterId) {

    var client = HttpClient.newHttpClient();

    var request =
        HttpRequest.newBuilder()
            .uri(URI.create(CHAPTER_API_BASE_URL + "/" + chapterId))
            .header("Accept", "application/json")
            .GET()
            .build();

    // Call API to get chapter detail
    AbstractChapter chapter =
        client
            .sendAsync(request, BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenApply(
                body -> {
                  try {

                    JsonNode jsonNode = objectMapper.readTree(body);
                    var data = jsonNode.get("data");
                    OtruyenChapterDetail chapterDetail =
                        objectMapper.treeToValue(data.get("item"), OtruyenChapterDetail.class);
                    String imageBaseUrl =
                        data.get("domain_cdn").asText() + chapterDetail.getChapterPath();

                    return OtruyenComicChapterAdapter.convertToComicChapter(
                        chapterDetail, comicId, imageBaseUrl);
                  } catch (JsonProcessingException e) {
                    throw new RuntimeException("Error when parsing chapter detail");
                  }
                })
            .join();

    if (chapter == null) {
      if (getNextService() != null) {
        return getNextService().getChapterDetailById(comicId, chapterId);
      }
    }

    return chapter;
  }
}
