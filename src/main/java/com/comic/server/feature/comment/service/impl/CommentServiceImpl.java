package com.comic.server.feature.comment.service.impl;

import com.comic.server.exceptions.ResourceNotFoundException;
import com.comic.server.feature.comment.dto.CommentResponse;
import com.comic.server.feature.comment.dto.NewCommentRequest;
import com.comic.server.feature.comment.model.Comment;
import com.comic.server.feature.comment.repository.CommentRepository;
import com.comic.server.feature.comment.repository.TreeCommentRepository;
import com.comic.server.feature.comment.service.CommentService;
import com.comic.server.feature.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;
  private final TreeCommentRepository treeCommentRepository;

  @Override
  @Transactional
  public CommentResponse addComment(NewCommentRequest newCommentRequest, User user) {

    String parentId = newCommentRequest.getReplyingTo();
    Comment parent =
        parentId == null
            ? null
            : commentRepository
                .findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException(Comment.class, "id", parentId));

    Comment comment =
        Comment.builder()
            .comicId(new ObjectId(newCommentRequest.getComicId()))
            .chapterId(new ObjectId(newCommentRequest.getChapterId()))
            .content(newCommentRequest.getContent())
            .build();

    if (parent != null) {
      comment.replyTo(parent);
    }

    return CommentResponse.from(insert(comment), user);
  }

  public Comment insert(Comment comment) {
    Comment newComment = commentRepository.insert(comment);
    if (newComment.getParentId() != null) {
      treeCommentRepository.increaseTotalRepliesAsync(newComment.getParentIds(), 1);
    }
    log.info("Comment saved: {}", newComment);
    return newComment;
  }

  @Override
  public Page<CommentResponse> fetchTopLevelReplies(String parentId, Pageable pageable) {
    return treeCommentRepository.findTopLevelReplies(parentId, pageable);
  }

  @Override
  public Page<?> fetchTopLevelComments(
      String comicId, String chapterId, String parentId, Pageable pageable) {
    return treeCommentRepository.findTopLevelComments(comicId, chapterId, parentId, pageable);
  }
}
