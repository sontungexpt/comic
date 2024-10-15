package com.comic.server.feature.comic.controller;

import com.comic.server.annotation.PublicEndpoint;
import com.comic.server.feature.comic.model.chapter.Chapter;
import com.comic.server.feature.comic.service.ChapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Chapter", description = "The set of endpoints for managing chapters")
@RequestMapping("/api/v1/chapters")
public class ChapterController {

  private final ChapterService chapterService;

  @GetMapping("/{chapterId}")
  @Operation(summary = "Get chapter by id", description = "Get chapter by id")
  @PublicEndpoint
  public Chapter getChapterDetailById(@PathVariable("chapterId") String chapterId) {
    return chapterService.getChapterDetailById(chapterId);
  }

  // @PostMapping
  // public ChapterResponse createChapter(
  //     @PathVariable Long comicId, @RequestBody ChapterRequest chapterRequest) {
  //   return chapterService.createChapter(comicId, chapterRequest);
  // }

  // @PutMapping("/{chapterId}")
  // public ChapterResponse updateChapter(
  //     @PathVariable Long comicId,
  //     @PathVariable Long chapterId,
  //     @RequestBody ChapterRequest chapterRequest) {
  //   return chapterService.updateChapter(comicId, chapterId, chapterRequest);
  // }

  // @DeleteMapping("/{chapterId}")
  // public void deleteChapter(@PathVariable Long comicId, @PathVariable Long chapterId) {
  //   chapterService.deleteChapter(comicId, chapterId);
  // }
}
