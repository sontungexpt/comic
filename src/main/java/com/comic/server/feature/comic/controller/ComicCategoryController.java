package com.comic.server.feature.comic.controller;

import com.comic.server.annotation.PublicEndpoint;
import com.comic.server.config.OpenApiConfig;
import com.comic.server.feature.comic.model.ComicCategory;
import com.comic.server.feature.comic.service.ComicCategoryService;
import com.comic.server.feature.user.enums.RoleType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class ComicCategoryController {

  private final ComicCategoryService comicCategoryService;

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

  @Operation(
      summary = "Create a new Category",
      description =
          "This endpoint creates a new category for a comic, and returns the created"
              + " category. This endpoint is only accessible to users with the 'ADMIN' role.")
  @PostMapping("")
  @RolesAllowed(RoleType.Fields.ADMIN)
  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_NAME)
  public ResponseEntity<?> createCategory(@RequestBody ComicCategory category) {
    return ResponseEntity.ok().body(comicCategoryService.createComicCategory(category));
  }

  @Operation(
      summary = "Create multiple Categories",
      description =
          "This endpoint creates multiple categories for a comic, and returns the created"
              + " categories.This endpoint is only accessible to users with the 'ADMIN' role.")
  @PostMapping("/bulk")
  @RolesAllowed(RoleType.Fields.ADMIN)
  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_NAME)
  public ResponseEntity<?> createCategories(@RequestBody Iterable<ComicCategory> categories) {
    return ResponseEntity.ok().body(comicCategoryService.createComicCategories(categories));
  }
}
