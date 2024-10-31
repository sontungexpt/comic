package com.comic.server.feature.comic.controller;

import com.comic.server.annotation.PublicEndpoint;
import com.comic.server.feature.comic.model.Comic;
import com.comic.server.feature.comic.model.chapter.AbstractChapter;
import com.comic.server.feature.comic.model.thirdparty.SourceName;
import com.comic.server.feature.comic.service.ChapterService;
import com.comic.server.feature.comic.service.ComicService;
import com.comic.server.feature.user.enums.RoleType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comics")
@RequiredArgsConstructor
@Slf4j
public class ComicController {

  private final ComicService comicService;
  private final ChapterService chapterService;

  @GetMapping("")
  @PublicEndpoint
  @Operation(summary = "Get all comics", description = "Get all comics with pagination")
  @PageableAsQueryParam
  public ResponseEntity<?> getComics(
      @Parameter(description = "The category ids to filter", required = false)
          @RequestParam(required = false)
          List<String> filterCategoryIds,
      @PageableDefault(page = 0, size = 24, sort = "dailyViews", direction = Sort.Direction.DESC)
          @Parameter(hidden = true)
          Pageable pageable) {
    return ResponseEntity.ok(comicService.getComicsWithCategories(pageable, filterCategoryIds));
  }

  @PostMapping("")
  @PublicEndpoint(
      profiles = {"dev"},
      filterJwt = true)
  @Operation(summary = "Create new comic", description = "Create new comic")
  public ResponseEntity<?> createNewComic(@RequestBody @Valid Comic comic) {
    try {

      return ResponseEntity.ok(comicService.createComic(comic));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
  }

  @GetMapping("/searching")
  @Operation(summary = "Search comics", description = "Search comics by keyword")
  @PublicEndpoint
  @PageableAsQueryParam
  public ResponseEntity<?> searchComics(
      @RequestParam String q,
      @PageableDefault(page = 0, size = 24, sort = "dailyViews", direction = Sort.Direction.DESC)
          @Parameter(hidden = true)
          Pageable pageable) {
    return ResponseEntity.ok(comicService.searchComic(q, pageable));
  }

  // public CompletableFuture<ResponseEntity<Page<ComicDTO>>> getComics(
  //     @RequestParam(required = false) List<String> filterCategoryIds,
  //     @PageableDefault(page = 0, size = 24, sort = "dailyViews", direction = Sort.Direction.DESC)
  //         Pageable pageable) {
  //   return comicService
  //       .getComicsWithCategories(pageable, filterCategoryIds)
  //       .thenApply(ResponseEntity::ok)
  //       .exceptionally(
  //           ex -> {
  //             log.error("Failed to fetch comics: ", ex);
  //             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Page.empty());
  //           });
  // }

  @GetMapping("/{comicId}")
  @PublicEndpoint
  @Operation(summary = "Get comic detail", description = "Get comic detail by comicId")
  @PageableAsQueryParam
  public ResponseEntity<?> getComicDetail(
      @PathVariable("comicId") String comicId,
      @RequestParam(required = true) SourceName sourceName,
      @PageableDefault(page = 0, size = 24, sort = "number", direction = Sort.Direction.ASC)
          @Parameter(hidden = true)
          Pageable pageable) {
    return ResponseEntity.ok(comicService.getComicDetail(comicId, sourceName, pageable));
  }

  @GetMapping("/{comicId}/chapters")
  @PublicEndpoint
  @Operation(summary = "Get chapters", description = "Get chapters by comicId")
  public ResponseEntity<?> getChaptersByComicId(@PathVariable("comicId") String comicId) {
    return ResponseEntity.ok(comicService.getChaptersByComicId(comicId));
  }

  @GetMapping("/{comicId}/chapters/{chapterId}")
  @Operation(summary = "Get chapter by id", description = "Get chapter by id")
  @PublicEndpoint
  public AbstractChapter getChapterDetailById(
      @PathVariable("comicId") String comicId, @PathVariable("chapterId") String chapterId) {
    return chapterService.getChapterDetailById(comicId, chapterId);
  }

  @PostMapping("/{comicId}/chapters")
  @Operation(summary = "Create chapter", description = "Create chapter of comic by comicId")
  @ResponseStatus(HttpStatus.CREATED)
  @RolesAllowed(RoleType.Fields.POSTER)
  public AbstractChapter createChapter(
      @PathVariable("comicId") String comicId, @RequestBody @Valid AbstractChapter chapter) {
    chapter.setComicId(comicId);
    return chapterService.createChapter(chapter);
  }
}
