package com.comic.server.feature.comic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comics/{comicId}/chapters")
public class ChapterController {

  // private final ChapterService chapterService;

  // @GetMapping
  // public List<ChapterResponse> getChapters(@PathVariable Long comicId) {
  //   return chapterService.getChapters(comicId);
  // }

  // @GetMapping("/{chapterId}")
  // public ChapterResponse getChapter(@PathVariable Long comicId, @PathVariable Long chapterId) {
  //   return chapterService.getChapter(comicId, chapterId);
  // }

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
