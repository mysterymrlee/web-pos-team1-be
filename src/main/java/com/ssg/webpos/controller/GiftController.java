package com.ssg.webpos.controller;

import com.ssg.webpos.domain.Delivery;
import com.ssg.webpos.domain.enums.DeliveryStatus;
import com.ssg.webpos.domain.enums.DeliveryType;
import com.ssg.webpos.dto.gift.GiftDTO;
import com.ssg.webpos.dto.gift.GiftRequestDTO;
import com.ssg.webpos.repository.delivery.DeliveryRedisImplRepository;
import com.ssg.webpos.repository.delivery.DeliveryRepository;
import com.ssg.webpos.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

  @PostMapping("/add")
  public ResponseEntity saveRedisGiftInfo(@RequestBody GiftRequestDTO giftRequestDTO) {
    List<GiftDTO> giftInfoList = giftRequestDTO.getGiftRecipientInfo();
    System.out.println("giftInfoList = " + giftInfoList);

    for (GiftDTO giftDTO : giftInfoList) {
      deliveryRedisImplRepository.saveGiftRecipientInfo(giftRequestDTO);
      System.out.println("giftDTO = " + giftDTO);
    }
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

  @PostMapping("/save")
  public ResponseEntity saveGiftInfo(@RequestBody GiftRequestDTO giftRequestDTO) {
    GiftDTO giftRecipientInfo = giftRequestDTO.getGiftRecipientInfo().get(0);
    Delivery delivery = new Delivery();
    String name = giftRecipientInfo.getName();
    String phoneNumber = giftRecipientInfo.getPhoneNumber();

    delivery.setUserName(name);
    delivery.setPhoneNumber(phoneNumber);
    delivery.setDeliveryType(DeliveryType.GIFT);
    delivery.setDeliveryStatus(DeliveryStatus.COMPLETE_PAYMENT);

    System.out.println("delivery = " + delivery);
    deliveryRepository.save(delivery);

    return new ResponseEntity(HttpStatus.OK);
  }
}
