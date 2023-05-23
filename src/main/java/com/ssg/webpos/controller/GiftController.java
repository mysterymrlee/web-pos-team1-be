package com.ssg.webpos.controller;

import com.ssg.webpos.dto.GiftRequestDTO;
import com.ssg.webpos.repository.delivery.DeliveryRedisImplRepository;
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

  @PostMapping("/add")
  public ResponseEntity saveGiftInfo(@RequestBody GiftRequestDTO giftRequestDTO) {
    try {
      deliveryRedisImplRepository.saveGiftRecipientInfo(giftRequestDTO);
      return new ResponseEntity(HttpStatus.CREATED);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("")
  public ResponseEntity getGiftInfo() {
    try {
      Map<String, Map<String, List<Object>>> all = deliveryRedisImplRepository.findAll();
      return new ResponseEntity(all, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  }

}
