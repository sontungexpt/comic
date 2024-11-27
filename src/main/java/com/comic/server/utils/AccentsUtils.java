package com.comic.server.utils;

/** AccentsUtils */
public class AccentsUtils {

  public static String removeAccents(String str) {
    return str.replaceAll("[áàảãạăắằẳẵặâấầẩẫậ]", "a")
        .replaceAll("[ÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬ]", "A")
        .replaceAll("[éèẻẽẹêếềểễệ]", "e")
        .replaceAll("[ÉÈẺẼẸÊẾỀỂỄỆ]", "E")
        .replaceAll("[óòỏõọôốồổỗộơớờởỡợ]", "o")
        .replaceAll("[ÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢ]", "O")
        .replaceAll("[úùủũụưứừửữự]", "u")
        .replaceAll("[ÚÙỦŨỤƯỨỪỬỮỰ]", "U")
        .replaceAll("[íìỉĩị]", "i")
        .replaceAll("[ÍÌỈĨỊ]", "I")
        .replaceAll("[ýỳỷỹỵ]", "y")
        .replaceAll("[ÝỲỶỸỴ]", "Y")
        .replaceAll("[đ]", "d")
        .replaceAll("[Đ]", "D");
  }
}
