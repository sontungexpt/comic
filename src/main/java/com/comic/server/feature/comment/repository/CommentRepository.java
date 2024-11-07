package com.comic.server.feature.comment.repository;

import com.comic.server.feature.comment.model.Comment;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

  List<Comment> findByComicIdAndChapterIdAndParentId(String comicId, String chapterId);
}
