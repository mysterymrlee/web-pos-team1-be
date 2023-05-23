package com.ssg.webpos.controller;

import com.ssg.webpos.domain.User;
import com.ssg.webpos.dto.CouponDTO;
import com.ssg.webpos.dto.CouponRequestDTO;
import com.ssg.webpos.dto.PointDTO;
import com.ssg.webpos.dto.PointRequestDTO;
import com.ssg.webpos.repository.cart.CartRedisRepository;
import com.ssg.webpos.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/gift-card")
public class CouponController {

  @Autowired
  CouponService couponService;
  @Autowired
  CartRedisRepository cartRedisRepository;

  @PostMapping("/valid")
  public ResponseEntity addCoupon(@RequestBody @Valid CouponRequestDTO requestDTO) throws Exception {
    String serialNumber = requestDTO.getSerialNumber();
    cartRedisRepository.saveCoupon(requestDTO);

    String validationMessage = couponService.validateCoupon(serialNumber);

    HttpStatus status;

    switch (validationMessage) {
      case "유효한 쿠폰입니다.":
        status = HttpStatus.OK;
        break;
      case "쿠폰이 만료되었습니다.":
        status = HttpStatus.PAYMENT_REQUIRED;
        break;
      case "이미 사용된 쿠폰입니다.":
        status = HttpStatus.BAD_REQUEST;
        break;
      default:
        status = HttpStatus.NOT_FOUND;
        break;
    }

    return new ResponseEntity<>(status);
  }
}
