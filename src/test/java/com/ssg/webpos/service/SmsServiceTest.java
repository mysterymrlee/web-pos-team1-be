package com.ssg.webpos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssg.webpos.domain.Delivery;
import com.ssg.webpos.domain.Order;
import com.ssg.webpos.dto.gift.GiftDeliveryAddressEntryDTO;
import com.ssg.webpos.dto.gift.GiftSmsDTO;
import com.ssg.webpos.dto.msg.MessageDTO;
import com.ssg.webpos.repository.delivery.DeliveryRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootTest
class SmsServiceTest {
  @Autowired
  SmsService smsService;
  @Autowired
  DeliveryRepository deliveryRepository;
  @Autowired
  OrderRepository orderRepository;

  @Test
  @DisplayName("문자 메시지 전송 테스트")
  void sendSmsTest() throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
    Long deliveryId = 62L;
    Long orderId = 10L;
    Delivery findDelivery = deliveryRepository.findById(deliveryId).get();
    System.out.println("findDelivery = " + findDelivery);
    Order findOrder = orderRepository.findById(orderId).get();
    System.out.println("findOrder = " + findOrder);
    String giftUrl = "http://localhost:3000/entry-address";

    MessageDTO msgDTO = new MessageDTO();
//    msgDTO.setTo("01012345678");
    msgDTO.setGiftUrl(giftUrl);
//    smsService.sendSms(msgDTO,findDelivery, findOrder);
  }

  @Test
  @DisplayName("문자 메시지 내용 테스트")
  void smsContentTest() {
    Long deliveryId = 62L;
    Long orderId = 10L;
    Delivery findDelivery = deliveryRepository.findById(deliveryId).get();
    Order findOrder = orderRepository.findById(orderId).get();
    String giftUrl = "http://localhost:3000/entry-address";
    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setGiftUrl(giftUrl);

    smsService.makeSmsContent(findDelivery, findOrder, messageDTO.getGiftUrl());
  }

  @Test
  @DisplayName("선물 받는 사람이 배송지 입력 완료 시, DB에 저장 테스트")
  void saveDeliveryAddress() {
    GiftDeliveryAddressEntryDTO giftDeliveryAddressEntryDTO = GiftDeliveryAddressEntryDTO.builder()
        .receiver("김진아")
        .phoneNumber("01025255555")
        .address("부산광역시 해운대구 우동")
        .build();
    smsService.saveDeliveryAddress(giftDeliveryAddressEntryDTO);
  }
}