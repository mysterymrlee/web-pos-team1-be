package com.ssg.webpos.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssg.webpos.domain.Delivery;
import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.enums.DeliveryStatus;
import com.ssg.webpos.domain.enums.DeliveryType;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.dto.gift.GiftSmsRequestDTO;
import com.ssg.webpos.dto.msg.MessageDTO;
import com.ssg.webpos.dto.msg.SmsResponseDTO;
import com.ssg.webpos.repository.delivery.DeliveryRepository;
import com.ssg.webpos.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/sms")
public class SmsController {
  @Autowired
  SmsService smsService;
  @Autowired
  DeliveryRepository deliveryRepository;

  public Delivery makeGiftInfo() {
    Delivery delivery = new Delivery();

    delivery.setUserName("홍길동");
    delivery.setPhoneNumber("01033335555");
    delivery.setSender("김진아");
    delivery.setDeliveryType(DeliveryType.GIFT);
    delivery.setDeliveryStatus(DeliveryStatus.COMPLETE_PAYMENT);

    System.out.println("delivery = " + delivery);
    Delivery savedDelivery = deliveryRepository.save(delivery);
    return savedDelivery;
  }

  public Order makeGiftOrder() {
    Order order = Order.builder()
        .orderStatus(OrderStatus.SUCCESS)
//        .totalQuantity(1)
        .orderName("제스프리 골드키위/봉 (200g내외)")
        .orderDate(LocalDateTime.now())
        .build();
    System.out.println("order = " + order);
    return order;
  }

  @PostMapping("/send")
  public SmsResponseDTO sendSms(@RequestBody MessageDTO messageDTO, Delivery savedDelivery, Order savedOrder) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
    SmsResponseDTO responseDTO = smsService.sendSms(messageDTO, savedDelivery, savedOrder);
    return responseDTO;
  }

  @PostMapping("/enter-address")
  public SmsResponseDTO enterAddress(@RequestBody MessageDTO messageDTO) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
    Delivery savedDelivery = makeGiftInfo();
    Order savedOrder = makeGiftOrder();
    SmsResponseDTO responseDTO = smsService.sendSms(messageDTO, savedDelivery, savedOrder);
    return responseDTO;
  }

  @PostMapping("/save-address")
  public ResponseEntity saveAddress(@RequestBody GiftSmsRequestDTO giftSmsRequestDTO) {
    smsService.saveDeliveryAddress(giftSmsRequestDTO);
    return new ResponseEntity(HttpStatus.CREATED);
  }
}
