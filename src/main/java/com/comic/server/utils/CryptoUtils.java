package com.comic.server.utils;

import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {

  private static final String ALGORITHM = "AES";

  public static String encrypt(String phoneNumber, String salt) {
    try {
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      Key key = new SecretKeySpec(salt.getBytes(), ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, key);
      byte[] encryptedBytes = cipher.doFinal(phoneNumber.getBytes());
      return Base64.getEncoder().encodeToString(encryptedBytes);

    } catch (Exception e) {
      System.out.println("Error while encrypting: " + e.toString());
      return null;
    }
  }

  public static String decryptPhoneNumber(String encryptedPhoneNumber, String salt)
      throws Exception {
    try {

      Cipher cipher = Cipher.getInstance(ALGORITHM);
      Key key = new SecretKeySpec(salt.getBytes(), ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, key);
      byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedPhoneNumber));
      return new String(decryptedBytes);
    } catch (Exception e) {
      System.out.println("Error while decrypting: " + e.toString());
      return null;
    }
  }
}
