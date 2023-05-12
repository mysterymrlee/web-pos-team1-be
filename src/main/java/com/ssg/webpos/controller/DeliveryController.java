package com.ssg.webpos.controller;

import com.ssg.webpos.domain.Delivery;
import com.ssg.webpos.dto.DeliveryAddDTO;
import com.ssg.webpos.repository.delivery.DeliveryRedisRepository;
import com.ssg.webpos.repository.delivery.DeliveryRepository;
import com.ssg.webpos.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/delivery")
public class DeliveryController {
  @Autowired
  DeliveryService deliveryService;
  @Autowired
  DeliveryRepository deliveryRepository;
  @Autowired
  DeliveryRedisRepository deliveryRedisRepository;

  @GetMapping("")
  public ResponseEntity getDeliveryInfo() {
    List<Delivery> all = deliveryRepository.findAll();
    return new ResponseEntity(all, HttpStatus.OK);
  }

  @PostMapping("/add")
  public ResponseEntity addDeliveryInfo(@RequestBody DeliveryAddDTO deliveryDTO) {
    //deliveryService.addDeliveryAddress(deliveryDTO);
    deliveryRedisRepository.save(deliveryDTO);
    return new ResponseEntity(HttpStatus.CREATED);
  }
}
