package com.comic.server.feature.comment.controller;

import com.comic.server.annotation.CurrentUser;
import com.comic.server.annotation.PageableQueryParams;
import com.comic.server.annotation.PublicEndpoint;
import com.comic.server.config.OpenApiConfig;
import com.comic.server.feature.comment.dto.NewCommentRequest;
import com.comic.server.feature.comment.service.CommentService;
import com.comic.server.feature.user.model.User;
import com.comic.server.validation.annotation.ObjectId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {

  private final CommentService commentService;

  @Operation(
      summary = "Get top level comments",
      description =
          "Fetch top-level comments associated with a comic or chapter. "
              + "If `parentId` is null, comments are grouped by `chapterId`. "
              + "Results are paginated by default.")
  @GetMapping("/top-level")
  @PageableQueryParams
  @PublicEndpoint
  public ResponseEntity<?> fetchTopLevelComments(
      @RequestParam(name = "comicId") @ObjectId String comicId,
      @RequestParam(name = "chapterId", required = false) @ObjectId String chapterId,
      @RequestParam(name = "parentId", required = false) @ObjectId String parentId,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(
        commentService.fetchTopLevelComments(comicId, chapterId, parentId, pageable));
  }

  @GetMapping("/{commentId}/replies/top-level")
  @Operation(
      summary = "Get replies of a comment",
      description =
          "Fetch replies for a specific comment, including only top-level replies. "
              + "Results are paginated by default.")
  @PageableQueryParams
  @PublicEndpoint
  public ResponseEntity<?> fetchTopLevelReplies(
      @PathVariable("commentId") @ObjectId String commentId,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(commentService.fetchTopLevelReplies(commentId, pageable));
  }

  @PostMapping("")
  @Operation(
      summary = "Add a new comment",
      description =
          "Post a new comment to a comic or chapter. Only users with the `POSTER` role are"
              + " authorized to add comments.",
      security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_NAME))
  @Profile("dev")
  @PublicEndpoint
  // @RolesAllowed(RoleType.Fields.POSTER)
  public ResponseEntity<?> addComment(
      @RequestBody @Valid NewCommentRequest newCommentRequest, @CurrentUser User user) {
    return ResponseEntity.ok(commentService.addComment(newCommentRequest, user));
  }
}
