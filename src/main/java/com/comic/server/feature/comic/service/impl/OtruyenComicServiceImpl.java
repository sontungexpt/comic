package com.comic.server.feature.comic.service.impl;

import com.comic.server.exceptions.ResourceNotFoundException;
import com.comic.server.feature.comic.dto.ComicDTO;
import com.comic.server.feature.comic.dto.ComicDetailDTO;
import com.comic.server.feature.comic.dto.otruyen.OtruyenChapterShortInfo;
import com.comic.server.feature.comic.dto.otruyen.OtruyenComic;
import com.comic.server.feature.comic.model.Source;
import com.comic.server.feature.comic.model.chapter.ShortInfoChapter;
import com.comic.server.feature.comic.service.GetComicService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtruyenComicServiceImpl implements GetComicService {

  private String BASE_URL = "https://otruyenapi.com/v1/api";
  private final ObjectMapper objectMapper;

  @SuppressWarnings("unchecked")
  private Optional<OtruyenComic> getOtruyenComicBySlug(String slug) {
    var client = HttpClient.newHttpClient();

    var request =
        HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/truyen-tranh/" + slug))
            .header("Accept", "application/json")
            .GET()
            .build();

    try {
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      Map<String, Object> map = objectMapper.readValue(response.body(), Map.class);
      OtruyenComic comic =
          objectMapper.convertValue(
              ((Map<String, Object>) map.get("data")).get("item"), OtruyenComic.class);
      return Optional.of(comic);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  @Override
  public ComicDetailDTO getComicDetail(String comicId, Pageable pageable) {
    return null;
  }

  @Override
  public Page<ComicDTO> getComicsWithCategories(Pageable pageable) {

    return null;
  }

  @Override
  public List<ShortInfoChapter> getChaptersByComicId(String slug) {
    var comic =
        getOtruyenComicBySlug(slug)
            .orElseThrow(() -> new ResourceNotFoundException(OtruyenComic.class, "slug", slug));

    var serverData = comic.getServerDatas();
    List<OtruyenChapterShortInfo> chapterShortInfos = serverData.get(0).getChapters();
    var result =
        chapterShortInfos.stream()
            .map(
                chapter ->
                    ShortInfoChapter.builder()
                        .comicId(new ObjectId(comic.getId()))
                        .name(chapter.getChapterTitle())
                        .num(Double.parseDouble(chapter.getChapterName()))
                        .originalSource(Source.builder().name("otruyen").build())
                        .build())
            .toList();
    return (List<ShortInfoChapter>) result;
  }
}
