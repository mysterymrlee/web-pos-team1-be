package com.ssg.webpos.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

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

  public static String rsaEncode(String plainText, String publicKey) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
    Cipher cipher = Cipher.getInstance(INSTANCE_TYPE);
    cipher.init(Cipher.ENCRYPT_MODE, convertPublicKey(publicKey));

    byte[] plainTextByte = cipher.doFinal(plainText.getBytes());
    return base64EncodeToString(plainTextByte);
  }

  public static String rsaDecode(String encryptedPlainText, String privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException {
    byte[] encryptedPlainTextByte = Base64.getDecoder().decode(encryptedPlainText.getBytes());

    Cipher cipher = Cipher.getInstance(INSTANCE_TYPE);
    cipher.init(Cipher.DECRYPT_MODE, convertPrivateKey(privateKey));

    return new String(cipher.doFinal(encryptedPlainTextByte));
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




//  public static PrivateKey getRsaPrivateKey(String base64PrivateKey) throws Exception {
//    byte[] privateKey = Base64.getDecoder().decode(base64PrivateKey);
//    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec((privateKey));
//    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//    return keyFactory.generatePrivate(keySpec);
//  }
//
//  public static byte[] decryptWithPrivateRsaKey(byte[] data, String rsaPrivateKeyBase64) throws Exception {
//    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//    cipher.init(Cipher.DECRYPT_MODE, getRsaPrivateKey(rsaPrivateKeyBase64));
//    return cipher.doFinal(data);
//  }
//
//  public static byte[] decryptWithAes(byte[] data, byte[] aesKey, byte[] iv) throws Exception {
//    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//    cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aesKey, "AES"), new IvParameterSpec(iv));
//    return cipher.doFinal(data);
//  }
}
