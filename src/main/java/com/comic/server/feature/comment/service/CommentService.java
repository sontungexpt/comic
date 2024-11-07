package com.comic.server.feature.comment.service;

import com.comic.server.feature.comment.dto.CommentResponse;
import com.comic.server.feature.comment.dto.NewCommentRequest;
import com.comic.server.feature.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public interface CommentService {

  CommentResponse addComment(@NonNull NewCommentRequest newCommentRequest, @NonNull User user);

  Page<CommentResponse> fetchTopLevelReplies(@NonNull String parentId, @NonNull Pageable pageable);

  Page<?> fetchTopLevelComments(
      @NonNull String comicId,
      @Nullable String chapterId,
      @Nullable String parentId,
      @NonNull Pageable pageable);
}
