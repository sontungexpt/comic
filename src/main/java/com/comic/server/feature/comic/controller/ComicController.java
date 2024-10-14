package com.comic.server.feature.comic.controller;

import com.comic.server.annotation.PublicEndpoint;
import com.comic.server.feature.comic.service.ComicService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comics")
@RequiredArgsConstructor
public class ComicController {

  private final ComicService comicService;

  @GetMapping("")
  @PublicEndpoint
  @Operation(summary = "Get all comics", description = "Get all comics with pagination")
  public ResponseEntity<?> getComics(
      @PageableDefault(page = 0, size = 20, sort = "rating", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return ResponseEntity.ok(comicService.getComicsWithCategories(pageable));
  }
}
