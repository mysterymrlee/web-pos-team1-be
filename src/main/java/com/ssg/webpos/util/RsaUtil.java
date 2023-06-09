package com.ssg.webpos.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class RsaUtil {
  private static final String INSTANCE_TYPE = "RSA";

  // 1024 bit RSA KeyPair 생성
  public static KeyPair generateRsaKeyPair() throws NoSuchAlgorithmException {
    KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(INSTANCE_TYPE);
    // 1024 bit long key
    keyPairGen.initialize(1024, new SecureRandom());
    // RSA key pair 생성 (public and private)
    return keyPairGen.genKeyPair();
  }

  public static List<String> rsaEncode(String publicKey, String... plainText) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
    Cipher cipher = Cipher.getInstance(INSTANCE_TYPE);
    // cipher 객체를 암호화 모드로 초기화
    cipher.init(Cipher.ENCRYPT_MODE, convertPublicKey(publicKey));
    String[] plainTexts = plainText;
    String result = "";
    List<String> list = new ArrayList<>();
    for (String text : plainTexts) {
      byte[] plainTextByte = cipher.doFinal(text.getBytes());
      result = base64EncodeToString(plainTextByte);
      list.add(result);
    }
    return list;
  }


  public static List<String> rsaDecode(String privateKey, List<String> encryptedPlainTextList) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException {
    Cipher cipher = Cipher.getInstance(INSTANCE_TYPE);
    // cipher 객체를 복호화 모드로 초기화
    cipher.init(Cipher.DECRYPT_MODE, convertPrivateKey(privateKey));
    List<String> list = new ArrayList<>();
    for (String text : encryptedPlainTextList) {
      byte[] encryptedPlainTextByte = Base64.getDecoder().decode(text.getBytes());
      list.add(new String(cipher.doFinal(encryptedPlainTextByte)));
    }
    return list;
  }

  // 비대칭키 PublicKey와 PrivateKey 생성 후 Client에게 공개키를 전달하거나
  // Server가 개인키를 보관하기 위한 목적으로 보통 String 변환하여 관리함
  public static PublicKey convertPublicKey(String publicKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
    KeyFactory keyFactory = KeyFactory.getInstance(INSTANCE_TYPE);
    byte[] publicKeyByte = Base64.getDecoder().decode(publicKey.getBytes());

    return keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyByte));
  }

  public static PrivateKey convertPrivateKey(String privateKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
    KeyFactory keyFactory = KeyFactory.getInstance(INSTANCE_TYPE);
    byte[] privateKeyByte = Base64.getDecoder().decode(privateKey.getBytes());

    return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyByte));
  }

  public static String base64EncodeToString(byte[] byteData) {
    return Base64.getEncoder().encodeToString(byteData);
  }
}
