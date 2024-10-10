package com.comic.server.feature.comic.controller;

import com.comic.server.annotation.PublicEndpoint;
import com.comic.server.feature.comic.service.ComicCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
public record ComicCategoryController(ComicCategoryService comicCategoryService) {

  @Operation(
      summary = "Retrieve Categories of Comics",
      description =
          "This endpoint retrieves the categories associated with a specific comic. "
              + "If the 'page' parameter is set to 0, all categories will be returned. "
              + "If the 'page' parameter is set to a positive integer, pagination will be applied "
              + "to return only the categories for that specific page.")
  @GetMapping("")
  @PublicEndpoint
  public ResponseEntity<?> getAllCategories(
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok()
        .body(
            pageable.getPageNumber() == 0
                ? comicCategoryService.getAllComicCategories()
                : comicCategoryService.getAllComicCategories(pageable));
  }
}
