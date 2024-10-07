package com.comic.server.utils;

import com.comic.server.common.regexp.PhoneNumberRegexp;
import java.util.Arrays;

public class PhoneNumberValidatorUtils {

  public static boolean validate(String phoneNumber) {
    return Arrays.stream(PhoneNumberRegexp.values())
        .anyMatch(regexp -> phoneNumber.matches(regexp.getValue()));
  }
}
