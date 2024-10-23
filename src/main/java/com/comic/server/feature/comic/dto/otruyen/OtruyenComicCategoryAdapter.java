package com.comic.server.feature.comic.dto.otruyen;

import com.comic.server.exceptions.ResourceNotFoundException;
import com.comic.server.feature.comic.model.ComicCategory;
import com.comic.server.feature.comic.repository.ComicCategoryRepository;
import com.comic.server.utils.ConsoleUtils;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OtruyenComicCategoryAdapter {

  private final ComicCategoryRepository comicCategoryRepository;

  Map<String, String> categoryNameMap =
      new HashMap<>() {
        {
          put("Action", "Action");
          put("Adventure", "Adventure");
          put("Anime", "Anime");
          put("Chuyển Sinh", "Chuyển Sinh");
          put("Comedy", "Comedy");
          put("Comic", "Comic");
          put("Cooking", "Cooking");
          put("Doujinshi", "Doujinshi");
          put("Drama", "Drama");
          put("Đam Mỹ", "Đam Mỹ");
          put("Ecchi", "Ecchi");
          put("Fantasy", "Fantasy");
          put("Gender Bender", "Gender Bender");
          put("Harem", "Harem");
          put("Historical", "Historical");
          put("Horror", "Horror");
          put("Josei", "Josei");
          put("Live action", "Live action");
          put("Manga", "Manga");
          put("Manhua", "Manhua");
          put("Manhwa", "Manhwa");
          put("Martial Arts", "Martial Arts");
          put("Mature", "Mature");
          put("Mecha", "Mecha");
          put("Mystery", "Mystery");
          put("One shot", "One shot");
          put("Psychological", "Psychological");
          put("Romance", "Romance");
          put("School Life", "School Life");
          put("Sci-fi", "Sci-fi");
          put("Seinen", "Seinen");
          put("Shoujo", "Shoujo");
          put("Shoujo Ai", "Shoujo Ai");
          put("Shounen", "Shounen");
          put("Shounen Ai", "Shounen Ai");
          put("Slice of Life", "Slice of Life");
          put("Smut", "Smut");
          put("Soft Yaoi", "Soft Yaoi");
          put("Soft Yuri", "Soft Yuri");
          put("Sports", "Sports");
          put("Supernatural", "Supernatural");
          put("Tạp Chí Truyện Tranh", "Tạp Chí Truyện Tranh");
          put("Thiếu Nhi", "Thiếu Nhi");
          put("Tragedy", "Tragedy");
          put("Trinh Thám", "Trinh Thám");
          put("Truyện Chữ", "Truyện Chữ");
          put("Truyện Màu", "Truyện Màu");
          put("Việt Nam", "Việt Nam");
          put("Webtoon", "Webtoon");
          put("Xuyên Không", "Xuyên Không");
          put("16+", "16+");
        }
      };

  Map<String, ComicCategory> categoryCache = new HashMap<>();

  public ComicCategory getUnknownCategory() {
    ComicCategory returnCategory =
        comicCategoryRepository
            .findByName("Unknown")
            .orElseGet(
                () ->
                    comicCategoryRepository.save(
                        ComicCategory.builder()
                            .name("Unknown")
                            .description("Unknown category")
                            .build()));

    categoryCache.put("Unknown", returnCategory);

    return returnCategory;
  }

  public ComicCategory convertToComicCategory(String categoryName) {
    if (categoryCache.containsKey(categoryName)) {
      ConsoleUtils.prettyPrint("Cache hit");
      return categoryCache.get(categoryName);
    }

    String mappedCategoryName = categoryNameMap.get(categoryName);
    if (mappedCategoryName != null) {
      ComicCategory returnCategory =
          comicCategoryRepository
              .findByName(mappedCategoryName)
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          ComicCategory.class, "name", mappedCategoryName));

      categoryCache.put(categoryName, returnCategory);
      return returnCategory;
    }

    return getUnknownCategory();
  }

  public ComicCategory convertToComicCategory(OtruyenCategory category) {
    return convertToComicCategory(category.getName());
  }
}
