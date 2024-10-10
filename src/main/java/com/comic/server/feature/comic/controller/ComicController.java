package com.comic.server.feature.comic.controller;

import com.comic.server.annotation.PublicEndpoint;
import com.comic.server.feature.comic.service.ComicService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comics")
public record ComicController(ComicService comicService) {

  @GetMapping("")
  @PublicEndpoint
  @Operation(summary = "Get all comics", description = "Get all comics with pagination")
  public ResponseEntity<?> getComics(@PageableDefault(page = 1, size = 20) Pageable pageable) {
    return ResponseEntity.ok(comicService.getComics(pageable));
  }
}
