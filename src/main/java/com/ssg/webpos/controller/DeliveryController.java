package com.ssg.webpos.controller;

import com.ssg.webpos.domain.Delivery;
import com.ssg.webpos.domain.PosStoreCompositeId;
import com.ssg.webpos.dto.DeliveryAddDTO;
import com.ssg.webpos.repository.delivery.DeliveryRedisImplRepository;
import com.ssg.webpos.repository.delivery.DeliveryRedisRepository;
import com.ssg.webpos.repository.delivery.DeliveryRepository;
import com.ssg.webpos.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    Map<String, Map<String, List<Object>>> findAll = null;
    try {
      findAll = deliveryRedisRepository.findAll();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return new ResponseEntity(findAll, HttpStatus.OK);
  }

  @PostMapping("/add")
  public ResponseEntity addDeliveryInfo(@RequestBody DeliveryAddDTO deliveryDTO) {
    //deliveryService.addDeliveryAddress(deliveryDTO);
    PosStoreCompositeId posStoreCompositeId = deliveryDTO.getPosStoreCompositeId();
    DeliveryAddDTO deliveryAddDTO = new DeliveryAddDTO();
    deliveryAddDTO.setPosStoreCompositeId(posStoreCompositeId);
    deliveryRedisRepository.save(deliveryAddDTO);
    return new ResponseEntity(HttpStatus.CREATED);
  }
}
