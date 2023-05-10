package com.ssg.webpos.controller;

import com.ssg.webpos.domain.Delivery;
import com.ssg.webpos.dto.DeliveryDTO;
import com.ssg.webpos.repository.DeliveryRepository;
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

  @GetMapping("")
  public ResponseEntity checkDelivery() {
    List<Delivery> all = deliveryRepository.findAll();
    return new ResponseEntity(all, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity add(@RequestBody DeliveryDTO deliveryDTO) {
    deliveryService.addDeliveryAddress(deliveryDTO);
    return new ResponseEntity(HttpStatus.OK);
  }
}
