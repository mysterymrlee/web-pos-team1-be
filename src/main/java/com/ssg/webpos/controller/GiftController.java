package com.ssg.webpos.controller;

import com.ssg.webpos.domain.PosStoreCompositeId;
import com.ssg.webpos.dto.delivery.GiftDTO;
import com.ssg.webpos.dto.delivery.GiftRequestDTO;
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
    Long posId = giftRequestDTO.getPosId();
    Long storeId = giftRequestDTO.getStoreId();
    System.out.println("posId = " + posId);
    System.out.println("storeId = " + storeId);

    List<GiftDTO> giftInfoList = giftRequestDTO.getGiftRecipientInfo();
    System.out.println("giftInfoList = " + giftInfoList);

    for(GiftDTO giftDTO : giftInfoList) {
      giftDTO.setPosStoreCompositeId(new PosStoreCompositeId(posId, storeId));
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

//  @PostMapping("/save")
//  public ResponseEntity saveGiftInfo(@RequestBody GiftRequestDTO giftRequestDTO) {
//
//  }
}
