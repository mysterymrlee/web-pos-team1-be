package com.ssg.webpos.controller;

import com.ssg.webpos.dto.gift.GiftSmsResponseDTO;
import com.ssg.webpos.service.CartService;
import com.ssg.webpos.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // @ResponseBody + @Controller
@RequestMapping("/api/v1/order")
public class OrderController {
  @Autowired
  SmsService smsService;
  @Autowired
  CartService cartService;

  @GetMapping("/get-gift-info/{orderSerialNumber}")
  public ResponseEntity getGiftInfo(@PathVariable String orderSerialNumber) {
    GiftSmsResponseDTO giftInfo = smsService.getGiftInfo(orderSerialNumber);
    return new ResponseEntity(giftInfo, HttpStatus.OK);
  }

  @GetMapping("/cancel/{merchantUid}")
  public ResponseEntity cancelOrder(@PathVariable String merchantUid) {
    try {
      cartService.cancelOrder(merchantUid);
      return new ResponseEntity(HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  }
}
