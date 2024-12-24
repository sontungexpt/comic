package com.comic.server.feature.comic.controller;

import com.comic.server.annotation.CurrentUser;
import com.comic.server.annotation.PageableQueryParams;
import com.comic.server.annotation.PublicEndpoint;
import com.comic.server.config.OpenApiConfig;
import com.comic.server.feature.comic.model.Comic;
import com.comic.server.feature.comic.model.chapter.AbstractChapter;
import com.comic.server.feature.comic.model.thirdparty.SourceName;
import com.comic.server.feature.comic.service.ChapterService;
import com.comic.server.feature.comic.service.ComicService;
import com.comic.server.feature.user.enums.RoleType;
import com.comic.server.feature.user.model.User;
import com.comic.server.validation.annotation.ObjectId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  @PageableQueryParams
  public ResponseEntity<?> getComics(
      @Parameter(description = "The category ids to filter", required = false)
          @RequestParam(required = false)
          List<@ObjectId String> filterCategoryIds,
      @PageableDefault(page = 0, size = 24, sort = "dailyViews", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return ResponseEntity.ok(comicService.getComicsWithCategories(pageable, filterCategoryIds));
  }

  @PostMapping("")
  @PublicEndpoint(profiles = {"dev"})
  @Operation(
      summary = "Create new comic",
      description = "Create new comic",
      security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_NAME))
  public ResponseEntity<?> createNewComic(@RequestBody @Valid Comic comic) {
    return ResponseEntity.ok(comicService.createComic(comic));
  }

  @GetMapping("/searching")
  @Operation(summary = "Search comics", description = "Search comics by keyword")
  @PublicEndpoint(filterJwt = true)
  @PageableQueryParams
  public ResponseEntity<?> searchComics(
      @RequestParam String q,
      @PageableDefault(page = 0, size = 24, sort = "dailyViews", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return ResponseEntity.ok(comicService.searchComic(q.trim(), pageable));
  }

  @GetMapping("/{comicId}")
  @Operation(summary = "Get comic detail", description = "Get comic detail by comicId")
  @PageableQueryParams
  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_NAME)
  @PublicEndpoint(filterJwt = true)
  public ResponseEntity<?> getComicDetail(
      @PathVariable("comicId") @ObjectId String comicId,
      @CurrentUser User user,
      @RequestParam(required = true) SourceName sourceName,
      @PageableDefault(page = 0, size = 50, sort = "num", direction = Sort.Direction.ASC)
          Pageable pageable) {
    return ResponseEntity.ok(comicService.getComicDetail(comicId, sourceName, pageable, user));
  }

  @GetMapping("/{comicId}/chapters")
  @PublicEndpoint
  @Operation(summary = "Get chapters", description = "Get chapters by comicId")
  public ResponseEntity<?> getChaptersByComicId(@ObjectId @PathVariable("comicId") String comicId) {
    return ResponseEntity.ok(comicService.getChaptersByComicId(comicId));
  }

  @GetMapping("/{comicId}/chapters/{chapterId}")
  @Operation(summary = "Get chapter by id", description = "Get chapter by id")
  @PublicEndpoint(filterJwt = true)
  public AbstractChapter getChapterDetailById(
      @ObjectId @PathVariable("comicId") String comicId,
      @PathVariable("chapterId") String chapterId,
      @CurrentUser User user) {
    return chapterService.getChapterDetailById(comicId, chapterId, user);
  }

  @PostMapping("/{comicId}/chapters")
  @Operation(summary = "Create chapter", description = "Create chapter of comic by comicId")
  @ResponseStatus(HttpStatus.CREATED)
  @RolesAllowed(RoleType.Fields.POSTER)
  @PublicEndpoint(profiles = {"dev"})
  public AbstractChapter createChapter(
      @ObjectId @PathVariable("comicId") String comicId,
      @CurrentUser User user,
      @RequestBody @Valid AbstractChapter chapter) {
    chapter.setComicId(comicId);
    return chapterService.createChapter(chapter, user);
  }

  @GetMapping("/{comicId}/chapters/first")
  @Operation(summary = "Get first chapter", description = "Get first chapter of comic by comicId")
  @PublicEndpoint(filterJwt = true)
  public AbstractChapter getFirstChapterDetail(
      @ObjectId @PathVariable("comicId") String comicId, @CurrentUser User user) {
    return chapterService.getFistChapterDetail(comicId, user);
  }

  @GetMapping("/{comicId}/chapters/lastest-read")
  @Operation(summary = "Get last chapter", description = "Get last chapter of comic by comicId")
  @PublicEndpoint(filterJwt = true)
  public AbstractChapter getLastestReadChapterDetail(
      @ObjectId @PathVariable("comicId") String comicId, @CurrentUser User user) {
    return chapterService.getLastestReadChapterDetail(comicId, user);
  }
}
