package com.comic.server.feature.comment.dto;

import com.comic.server.validation.annotation.ObjectId;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class NewCommentRequest {

  @Schema(description = "The content of the comment", example = "This is a comment")
  @NotBlank
  private String content;

  @Schema(
      description =
          "The id of the comic that the comment replys to (if any)"
              + "if not provided, the comment is a root comment",
      nullable = true,
      requiredMode = RequiredMode.NOT_REQUIRED)
  @ObjectId
  @Nullable
  private String replyingTo;

  @Schema(
      description =
          "The id of the chapter that the comment belongs to "
              + "(if any), if not provided, the comment belongs to the comic",
      type = "String",
      nullable = true,
      example = "60f3b3b3b3b3b3b3b3b3b3b3",
      requiredMode = RequiredMode.NOT_REQUIRED)
  @ObjectId
  @Nullable
  private String chapterId;

  @Schema(
      description = "The id of the comic that the comment belongs to",
      format = "ObjectId",
      type = "String",
      example = "60f3b3b3b3b3b3b3b3b3b3b3",
      requiredMode = RequiredMode.REQUIRED)
  @NotBlank
  @ObjectId
  private String comicId;
}
