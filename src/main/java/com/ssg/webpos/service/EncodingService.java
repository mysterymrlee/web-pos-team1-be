package com.ssg.webpos.service;

import com.ssg.webpos.domain.Delivery;
import com.ssg.webpos.domain.encodingTest.EncodingDeliveryAddress;
import com.ssg.webpos.dto.encode.EncodeDTO;
import com.ssg.webpos.repository.delivery.DeliveryAddressRepository;
import com.ssg.webpos.repository.delivery.DeliveryRepository;
import com.ssg.webpos.repository.test.EncodingDeliveryAddressRepository;
import com.ssg.webpos.util.RsaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.transaction.Transactional;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EncodingService {
  private final DeliveryRepository deliveryRepository;
  private final DeliveryAddressRepository deliveryAddressRepository;
  private final EncodingDeliveryAddressRepository encodingDeliveryAddressRepository;

  @Value("${public_key}")
  private String publicKey;
  @Value("${private_key}")
  private String privateKey;

  /**
   * 1. 암호화용 domain하나 만들어서 이름, 전화번호, 주소, 우편번호 암호화해서 저장 Test
   * 2.
   */
  @Transactional
  public void saveEncodedDeliveryAddressData(EncodeDTO encodeDTO) throws NoSuchPaddingException, IllegalBlockSizeException, InvalidKeySpecException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
    String userName = encodeDTO.getUserName();
    String address = encodeDTO.getAddress();
    String phoneNumber = encodeDTO.getPhoneNumber();
    String postCode = encodeDTO.getPostCode();

    List<String> encodedResult = RsaUtil.rsaEncode(publicKey, userName, address, phoneNumber, postCode);
    System.out.println("암호화 결과 = " + encodedResult);

    List<String> decodedResult = RsaUtil.rsaDecode(privateKey, encodedResult);
    System.out.println("복호화 결과 = " + decodedResult);

    EncodingDeliveryAddress encodingDeliveryAddress = EncodingDeliveryAddress.builder()
        .userName(encodedResult.get(0))
        .address(encodedResult.get(1))
        .phoneNumber(encodedResult.get(2))
        .postCode(encodedResult.get(3))
        .build();

    encodingDeliveryAddressRepository.save(encodingDeliveryAddress);
  }
}
