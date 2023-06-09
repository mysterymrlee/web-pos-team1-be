package com.ssg.webpos.service;

import com.ssg.webpos.dto.encode.EncodeDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.transaction.Transactional;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@SpringBootTest
@Transactional
public class EncodingServiceTest {
  @Autowired
  EncodingService encodingService;
  @Test
  @DisplayName("RSA DeliveryAddress 암호화, 복호화 테스트")
  void saveEncodedDeliveryAddressDataTest() throws NoSuchPaddingException, IllegalBlockSizeException, InvalidKeySpecException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
    EncodeDTO encodeDTO = EncodeDTO.builder()
        .userName("김진아")
        .address("부산광역시 부산진구")
        .postCode("12345")
        .phoneNumber("01012345678")
        .build();
    encodingService.saveEncodedDeliveryAddressData(encodeDTO);
  }
}
