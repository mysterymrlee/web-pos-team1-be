package com.ssg.webpos.controller;

import com.ssg.webpos.domain.Delivery;
import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.enums.DeliveryStatus;
import com.ssg.webpos.domain.enums.DeliveryType;
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
  @PostMapping("/save-info")
  public ResponseEntity saveGiftInfo(@RequestBody GiftRequestDTO giftRequestDTO, Order savedOrder) {
    Long storeId = giftRequestDTO.getStoreId();
    Long posId = giftRequestDTO.getPosId();
    String compositeId = storeId + "-" + posId;
    List<Map<String, Object>> giftRecipientInfoList = deliveryRedisImplRepository.findGiftRecipientInfo(compositeId);

    String serialNumber = deliveryService.makeSerialNumber(savedOrder.getId());
    for (Map<String, Object> giftRecipient : giftRecipientInfoList) {
      Delivery delivery = Delivery.builder()
          .userName((String) giftRecipient.get("receiver"))
          .phoneNumber((String) giftRecipient.get("phoneNumber"))
          .sender((String) giftRecipient.get("sender"))
          .deliveryType(DeliveryType.GIFT)
          .serialNumber(serialNumber)
          .deliveryStatus(DeliveryStatus.COMPLETE_PAYMENT)
          .build();
      System.out.println("delivery = " + delivery);
      deliveryRepository.save(delivery);
    }

    return new ResponseEntity(giftRecipientInfoList, HttpStatus.OK);
  }

//  @PostMapping("/")
}
