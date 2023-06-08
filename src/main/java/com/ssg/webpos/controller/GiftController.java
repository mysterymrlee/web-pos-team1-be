package com.ssg.webpos.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssg.webpos.domain.Delivery;
import com.ssg.webpos.domain.Order;
import com.ssg.webpos.dto.PaymentsDTO;
import com.ssg.webpos.dto.gift.GiftRequestDTO;
import com.ssg.webpos.dto.msg.MessageDTO;
import com.ssg.webpos.repository.delivery.DeliveryRedisImplRepository;
import com.ssg.webpos.repository.delivery.DeliveryRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import com.ssg.webpos.service.DeliveryService;
import com.ssg.webpos.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/gift")
public class GiftController {
  @Autowired
  DeliveryRedisImplRepository deliveryRedisImplRepository;
  @Autowired
  DeliveryService deliveryService;
  @Autowired
  DeliveryRepository deliveryRepository;
  @Autowired
  SmsService smsService;
  @Autowired
  OrderRepository orderRepository;

  @PostMapping("/add")
  public ResponseEntity saveRedisGiftInfo(@RequestBody GiftRequestDTO giftRequestDTO) {
    deliveryRedisImplRepository.saveGiftRecipientInfo(giftRequestDTO);
    return new ResponseEntity(HttpStatus.CREATED);
  }

  @GetMapping("")
  public ResponseEntity getRedisGiftInfo() {
    try {
      Map<String, Map<String, List<Object>>> all = deliveryRedisImplRepository.findAll();
      return new ResponseEntity(all, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  }

  // redis에서 가져와서 db에 저장
  // * 배송지 입력 sms 전송 구현 필요 *
  @PostMapping("/save-info")
  public ResponseEntity saveGiftInfo(@RequestBody PaymentsDTO paymentsDTO, MessageDTO messageDTO) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
    Delivery savedDelivery = deliveryService.saveGiftInfo(paymentsDTO);
    System.out.println("savedDelivery = " + savedDelivery);
    String phoneNumber = savedDelivery.getPhoneNumber();
    messageDTO.setTo(phoneNumber);
    Order findOrder = orderRepository.findByDeliveryId(savedDelivery.getId());
    System.out.println("findOrder = " + findOrder);
    smsService.sendSms(messageDTO, savedDelivery, findOrder);
    return new ResponseEntity(savedDelivery, HttpStatus.OK);
  }
}
