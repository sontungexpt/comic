package com.comic.server.support;

import static org.junit.jupiter.api.Assertions.*;

import com.comic.server.common.structure.BoundedPriorityQueue;
import com.comic.server.common.structure.BoundedPriorityQueue.Strategy;
import com.comic.server.feature.comic.model.chapter.Chapter;
import com.comic.server.feature.comic.model.chapter.ShortInfoChapter;
import com.comic.server.feature.comic.support.NewChapterComparator;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BoundedPriorityQueueTest {

  private BoundedPriorityQueue<Integer> intQueue;
  private BoundedPriorityQueue<Chapter> chapterQueue;

  @BeforeEach
  public void setUp() {
    intQueue = new BoundedPriorityQueue<>(3, Integer::compareTo);
    intQueue.add(5);
    intQueue.add(1);
    intQueue.add(3);

    chapterQueue = new BoundedPriorityQueue<>(3, new NewChapterComparator());
    chapterQueue.add(ShortInfoChapter.builder().id("1").name("Xin chao").num(1.0).build());
    chapterQueue.add(ShortInfoChapter.builder().id("2").name("Xin chao2").num(2.0).build());
    chapterQueue.add(ShortInfoChapter.builder().id("3").name("Xin chao3").num(3.0).build());
  }

  @Test
  public void testAddElement() {
    // Kiểm tra kích thước của queue
    assertEquals(3, intQueue.size());

    // Kiểm tra xem phần tử đã được thêm vào queue chưa
    assertTrue(intQueue.contains(5));
    assertTrue(intQueue.contains(1));
    assertTrue(intQueue.contains(3));
  }

  @Test
  public void testAddMoreThanMaxSize() {
    // Thêm nhiều phần tử hơn maxSize và kiểm tra rằng phần tử nhỏ nhất bị loại bỏ
    intQueue.add(6);
    // Queue có maxSize = 3, nên phần tử nhỏ nhất là 1 sẽ bị loại bỏ
    assertEquals(3, intQueue.size());
    assertFalse(intQueue.contains(1));
    assertTrue(intQueue.contains(3));
    assertTrue(intQueue.contains(5));
    assertTrue(intQueue.contains(6));
  }

  @Test
  public void testUniqueElements() {
    // Cài đặt chế độ unique và thêm các phần tử trùng lặp
    intQueue.setStrategy(Strategy.UNIQUE);
    boolean addedDuplicate = intQueue.add(3); // Phần tử 3 đã có trong queue, nên không thêm được
    assertFalse(addedDuplicate);
    assertEquals(3, intQueue.size()); // Vẫn giữ nguyên 3 phần tử duy nhất
    assertTrue(intQueue.contains(3));
    assertTrue(intQueue.contains(5));
    assertTrue(intQueue.contains(1));
  }

  @Test
  public void testUpdateIfExists() {
    // Thêm phần tử với chế độ updateIfExists
    intQueue.setStrategy(Strategy.UPDATE_IF_EXISTS);

    // Cập nhật phần tử 1 bằng cách loại bỏ và thêm lại
    boolean updated = intQueue.add(1); // 1 đã có trong queue, nên nó sẽ được cập nhật
    assertTrue(updated);
    assertEquals(3, intQueue.size()); // Kích thước không đổi
    assertTrue(intQueue.contains(1)); // Phần tử 1 vẫn tồn tại
    assertTrue(intQueue.contains(3));
    assertTrue(intQueue.contains(5));
  }

  @Test
  public void testPoll() {
    assertEquals(1, intQueue.poll()); // 1 là phần tử nhỏ nhất và sẽ bị loại bỏ
    assertEquals(2, intQueue.size()); // Kích thước còn lại là 2

    // Kiểm tra xem phần tử 1 đã bị loại bỏ chưa
    assertFalse(intQueue.contains(1));

    // Kiểm tra xem phần tử nhỏ nhất hiện tại là gì
    assertEquals(3, intQueue.peek());
  }

  @Test
  public void testIsEmpty() {
    intQueue.clear();
    // Kiểm tra khi queue rỗng
    assertTrue(intQueue.isEmpty());
    intQueue.add(5);
    assertFalse(intQueue.isEmpty());
    assertEquals(1, intQueue.size());
    assertEquals(5, intQueue.peek());
  }

  @Test
  public void testAddNullElement() {
    // Kiểm tra không thể thêm phần tử null
    assertFalse(intQueue.add(null));
  }

  @Test
  public void testComparator() {
    // Kiểm tra với một Comparator khác (Comparator giảm dần)
    intQueue = new BoundedPriorityQueue<>(3, Collections.reverseOrder(Integer::compareTo));
    intQueue.add(5);
    intQueue.add(1);
    intQueue.add(3);

    assertEquals(5, intQueue.peek()); // Phần tử đầu tiên phải là 5 vì dùng Comparator giảm dần
  }

  @Test
  public void testAddChapters() {
    assertEquals(3, chapterQueue.size());

    assertTrue(chapterQueue.contains(ShortInfoChapter.builder().id("1").build()));
    assertTrue(chapterQueue.contains(ShortInfoChapter.builder().id("2").build()));
    assertTrue(chapterQueue.contains(ShortInfoChapter.builder().id("3").build()));
  }

  @Test
  public void testAddMoreThanMaxSizeChapters() {
    chapterQueue.add(ShortInfoChapter.builder().id("4").name("Xin chao4").num(4.0).build());
    assertEquals(3, chapterQueue.size());
    assertTrue(
        chapterQueue.contains(
            ShortInfoChapter.builder().id("2").name("Xin chao2").num(2.0).build()));
    assertTrue(
        chapterQueue.contains(
            ShortInfoChapter.builder().id("3").name("Xin chao3").num(3.0).build()));
    assertTrue(
        chapterQueue.contains(
            ShortInfoChapter.builder().id("4").name("Xin chao4").num(4.0).build()));
  }

  @Test
  public void testUniqueElementsChapters() {
    chapterQueue.setStrategy(Strategy.UNIQUE);
    boolean addedDuplicate =
        chapterQueue.add(ShortInfoChapter.builder().id("3").name("Xin chao5").num(3.0).build());
    assertFalse(addedDuplicate);
    assertEquals(3, chapterQueue.size());
    assertTrue(chapterQueue.contains(ShortInfoChapter.builder().id("3").build()));
    assertTrue(chapterQueue.contains(ShortInfoChapter.builder().id("1").build()));
    assertTrue(chapterQueue.contains(ShortInfoChapter.builder().id("2").build()));
    // assertFalse(
    //     chapterQueue.stream()
    //         .anyMatch(
    //             c -> {
    //               if (c.getId().equals("3")) {
    //                 return c.getName().equals("Xin chao5");
    //               }
    //               return false;
    //             }));
  }

  @Test
  public void testUpdateIfExistsChapters() {
    chapterQueue.setStrategy(Strategy.UPDATE_IF_EXISTS);
    boolean updated =
        chapterQueue.add(ShortInfoChapter.builder().id("3").name("Xin chao5").num(3.0).build());
    assertTrue(updated);
    assertEquals(3, chapterQueue.size());
    assertTrue(
        chapterQueue.contains(
            ShortInfoChapter.builder().id("3").name("Xin chao3").num(3.0).build()));
    assertTrue(
        chapterQueue.contains(
            ShortInfoChapter.builder().id("2").name("Xin chao2").num(2.0).build()));
    assertTrue(
        chapterQueue.contains(
            ShortInfoChapter.builder().id("1").name("Xin chao").num(1.0).build()));
    assertTrue(
        chapterQueue.stream()
            .anyMatch(
                c -> {
                  if (c.getId().equals("3")) {
                    return c.getName().equals("Xin chao5");
                  }
                  return false;
                }));
  }

  @Test
  public void testPollChapters() {
    assertEquals(
        ShortInfoChapter.builder().id("1").name("Xin chao").num(1.0).build(), chapterQueue.poll());
    assertEquals(2, chapterQueue.size());
    assertFalse(
        chapterQueue.contains(
            ShortInfoChapter.builder().id("1").name("Xin chao").num(1.0).build()));
    assertTrue(
        chapterQueue.contains(
            ShortInfoChapter.builder().id("2").name("Xin chao2").num(2.0).build()));
    assertTrue(
        chapterQueue.contains(
            ShortInfoChapter.builder().id("3").name("Xin chao3").num(3.0).build()));
  }

  @Test
  public void testIsEmptyChapters() {
    chapterQueue.clear();
    assertTrue(chapterQueue.isEmpty());
    chapterQueue.add(ShortInfoChapter.builder().id("1").name("Xin chao").num(1.0).build());
    assertEquals(1, chapterQueue.size());
    assertEquals(
        ShortInfoChapter.builder().id("1").name("Xin chao").num(1.0).build(), chapterQueue.peek());
  }
}
