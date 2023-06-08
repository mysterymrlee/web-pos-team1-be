package com.ssg.webpos.controller;

import com.ssg.webpos.dto.gift.GiftSmsResponseDTO;
import com.ssg.webpos.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // @ResponseBody + @Controller
@RequestMapping("/api/v1/order")
public class OrderController {
  @Autowired
  SmsService smsService;

  @GetMapping("/get-gift-info/{orderSerialNumber}")
  public ResponseEntity getGiftInfo(@PathVariable String orderSerialNumber) {
    GiftSmsResponseDTO giftInfo = smsService.getGiftInfo(orderSerialNumber);
    return new ResponseEntity(giftInfo, HttpStatus.OK);
  }
}
