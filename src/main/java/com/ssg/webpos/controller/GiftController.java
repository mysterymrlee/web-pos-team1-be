package com.ssg.webpos.controller;

import com.ssg.webpos.repository.delivery.DeliveryRedisImplRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/gift")
public class GiftController {
  @Autowired
  DeliveryRedisImplRepository deliveryRedisImplRepository;


}
