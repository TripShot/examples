package com.tripshot.example;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class BadgeDecryptExample {

  public static void main(String[] args) {
    if (args.length != 2) {
      System.err.println("usage : <badging key> <encryptedBadge>");
      Runtime.getRuntime().exit(1);
    }

    System.out.println(decrypt(args[0], args[1]));
  }

  public static String decrypt(String badgingKey, String encryptedBadge) {
    try {

      String parts[] = encryptedBadge.split(":", 2);
      byte[] initVector = Base64.getDecoder().decode(parts[0]);
      byte[] cipherText = Base64.getDecoder().decode(parts[1]);

      IvParameterSpec iv = new IvParameterSpec(initVector);
      SecretKeySpec keySpec = new SecretKeySpec(badgingKey.getBytes("UTF-8"), "AES");

      // We use PKCS7 padding. Java's crypto provider supports PKCS7 via the name "PKCS5PADDING" which is not the correct name
      // but apparently is this way for legacy reasons.
      // See https://crypto.stackexchange.com/questions/9043/what-is-the-difference-between-pkcs5-padding-and-pkcs7-padding
      // If you are porting this to another crypto library, and that library has PKCS7 support, use that instead since PKCS5 per spec won't work with AES.
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
      cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
      byte[] original = cipher.doFinal(cipherText);

      return new String(original);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new RuntimeException(ex);
    }
  }
}
