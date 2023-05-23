package com.ssg.webpos.service;

import com.ssg.webpos.domain.Coupon;
import com.ssg.webpos.domain.enums.CouponStatus;
import com.ssg.webpos.dto.CouponDTO;
import com.ssg.webpos.dto.CouponRequestDTO;
import com.ssg.webpos.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class CouponService {
  @Autowired
  CouponRepository couponRepository;

  public CouponService(CouponRepository couponRepository) {
  }


//  public Coupon getCouponInfoById(Long couponId) {
//    Optional<Coupon> couponOptional = couponRepository.findById(couponId);
//
//  }
  // 쿠폰 유효성
  public String validateCoupon(String serialNumber) {
    Optional<Coupon> couponOptional = couponRepository.findBySerialNumber(serialNumber);

    if (couponOptional.isPresent()) {
      Coupon coupon = couponOptional.get();
      LocalDate expiredDate = couponOptional.get().getExpiredDate();
      LocalDate currentDate = LocalDate.now();
      CouponStatus couponStatus = coupon.getCouponStatus();

      if (expiredDate.isBefore(currentDate)) {
        return "쿠폰이 만료되었습니다.";
      } else if (couponStatus != CouponStatus.NOT_USED) {
        return "이미 사용된 쿠폰입니다.";
      } else {
        return "유효한 쿠폰입니다.";
      }
    } else {
      return "쿠폰을 찾을 수 없습니다.";
    }
  }
  // status NOT_USED -> USED
  public void updateCouponStatusToUsed(Long couponId) {
    Optional<Coupon> couponOptional = couponRepository.findById(couponId);
    if (couponOptional.isPresent()) {
      Coupon coupon = couponOptional.get();
      coupon.setCouponStatus(CouponStatus.USED);
      couponRepository.save(coupon);
    } else {
      throw new IllegalArgumentException("Invalid coupon ID");
    }
  }

  }
