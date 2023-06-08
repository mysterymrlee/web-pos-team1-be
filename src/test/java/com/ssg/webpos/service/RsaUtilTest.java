package com.ssg.webpos.service;

import com.ssg.webpos.util.RsaUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.security.KeyPair;


@Transactional
@SpringBootTest
class RsaUtilTest {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static String publicKey;
  private static String privateKey;

  @Test
  @DisplayName("1024 bit RSA KeyPair 생성")
  void generateKeyPair() throws Exception {
    KeyPair keyPair = RsaUtil.generateRsaKeyPair();

    publicKey = RsaUtil.base64EncodeToString(keyPair.getPublic().getEncoded());
    privateKey = RsaUtil.base64EncodeToString(keyPair.getPrivate().getEncoded());
    System.out.println("keyPair = " + keyPair);
    System.out.println("publicKey = " + publicKey);
    System.out.println("privateKey = " + privateKey);
  }

}