package com.comic.server.feature.comic.model;

import io.swagger.v3.oas.annotations.media.Schema;

public enum ComicTeamRole {
  @Schema(
      description =
          "Permission with full access to all comics, including managing team member roles and"
              + " permissions, but excluding the ability to delete the comic")
  // Quyền truy cập đầy đủ vào tất cả các truyện, bao gồm quản lý quyền và vai trò
  // của các thành viên trong team, nhưng không bao gồm quyền xóa truyện.
  COMIC_MANAGER,

  @Schema(
      description =
          "Permission to manage the roles and permissions of team members, including assigning and"
              + " updating access levels, but excluding content management and deletion of comics")
  // Quyền quản lý vai trò và quyền của các thành viên trong team, bao gồm việc
  // gán và cập nhật cấp độ truy cập, nhưng không bao gồm việc quản lý nội dung
  // hoặc xóa truyện.
  PERMISSION_MANAGER,

  @Schema(
      description =
          "Permission to edit chapter-level information, such as the title, description, and"
              + " sequence of the chapters")
  // Người biên tập chapter, có quyền chỉnh sửa thông tin chapter như tên, mô tả, và
  // thứ tự của các chapter
  CHAPTER_EDITOR,

  @Schema(description = "Permission to delete chapters")
  // Người xóa chapter
  CHAPTER_DELETE,

  @Schema(
      description = "Responsible for managing the comments, discussions, and feedback from readers")
  // Người quản lý và duyệt các bình luận
  COMMENT_MODERATOR,

  @Schema(
      description =
          "Responsible for the overall review and approval of the comic content before release")
  CONTENT_REVIEWER, // Người duyệt nội dung truyện
}
