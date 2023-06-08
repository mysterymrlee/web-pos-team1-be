package com.ssg.webpos.service;

import com.ssg.webpos.domain.Delivery;
import com.ssg.webpos.domain.DeliveryAddress;
import com.ssg.webpos.repository.delivery.DeliveryRepository;
import com.ssg.webpos.util.RsaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class EncodingService {
  private final DeliveryRepository deliveryRepository;

  public void encodingDeliveryInfo(Long id) {
    Delivery findDelivery = deliveryRepository.findById(id).get();

  }

  public void generateRsaKeyPair(DeliveryAddress deliveryAddress) throws NoSuchAlgorithmException {
    KeyPair keyPair = RsaUtil.generateRsaKeyPair();

    byte[] publicKey = keyPair.getPublic().getEncoded();
    byte[] privateKey = keyPair.getPrivate().getEncoded();

    // REST API를 통해 공개 키를 전송할 수 있도록 Base64 텍스트 형식의 인코딩 키
    String rsaPublicKeyBase64 = new String(Base64.getEncoder().encode(publicKey));
    String rsaPrivateKeyBase64 = new String(Base64.getEncoder().encode(privateKey));

    // 나중에 사용할 수 있도록 사용자 개체에 키 저장

  }
}
