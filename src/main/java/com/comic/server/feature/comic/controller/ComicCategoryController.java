package com.comic.server.feature.comic.controller;

import com.comic.server.annotation.PublicEndpoint;
import com.comic.server.config.OpenApiConfig;
import com.comic.server.feature.comic.model.ComicCategory;
import com.comic.server.feature.comic.service.ComicCategoryService;
import com.comic.server.feature.user.enums.RoleType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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
              + "This endpoint is accessible to all users.")
  @GetMapping("")
  @PublicEndpoint
  public ResponseEntity<?> getAllCategories() {
    return ResponseEntity.ok().body(comicCategoryService.getAllComicCategories());
  }

  @Operation(
      summary = "Create a new Category",
      description =
          "This endpoint creates a new category for a comic, and returns the created"
              + " category. This endpoint is only accessible to users with the 'ADMIN' role.")
  @PostMapping("")
  @RolesAllowed(RoleType.Fields.ADMIN)
  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_NAME)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<?> createCategory(@Valid @RequestBody ComicCategory category) {
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
  public ResponseEntity<?> createCategories(
      @RequestBody Iterable<@Valid ComicCategory> categories) {
    return ResponseEntity.ok().body(comicCategoryService.createComicCategories(categories));
  }
}
