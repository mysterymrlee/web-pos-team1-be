package com.ssg.webpos.controller;

import com.ssg.webpos.dto.coupon.CouponAddRequestDTO;
import com.ssg.webpos.dto.coupon.CouponAddResponseDTO;
import com.ssg.webpos.repository.cart.CartRedisRepository;
import com.ssg.webpos.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1/gift-card")
public class CouponController {

  @Autowired
  CouponService couponService;
  @Autowired
  CartRedisRepository cartRedisRepository;

  @PostMapping("/valid")
  public ResponseEntity addCoupon(@RequestBody @Valid CouponAddRequestDTO requestDTO) throws Exception {
    String serialNumber = requestDTO.getSerialNumber();
    cartRedisRepository.saveCoupon(requestDTO);
    int deductedPrice = couponService.getCouponInfoBySerialNumber(serialNumber).getDeductedPrice();
    CouponAddResponseDTO couponResponseDTO = new CouponAddResponseDTO(deductedPrice);

    String validationMessage = couponService.validateCoupon(serialNumber);
    HttpStatus status;

    switch (validationMessage) {
      case "유효한 쿠폰입니다.":
        status = HttpStatus.OK; // 200
        break;
      case "쿠폰이 만료되었습니다.":
        status = HttpStatus.PAYMENT_REQUIRED; // 402
        break;
      case "이미 사용된 쿠폰입니다.":
        status = HttpStatus.BAD_REQUEST; // 400
        break;
      default: // 쿠폰을 찾을 수 없는 경우
        status = HttpStatus.NOT_FOUND; // 404
        break;
    }

    return new ResponseEntity(couponResponseDTO, status);
  }
}
