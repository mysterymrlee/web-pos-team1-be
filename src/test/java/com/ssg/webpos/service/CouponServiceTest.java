package com.ssg.webpos.service;
import com.ssg.webpos.domain.Coupon;
import com.ssg.webpos.domain.enums.CouponStatus;
import com.ssg.webpos.repository.CouponRepository;
import com.ssg.webpos.repository.cart.CartRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class CouponServiceTest {
  @Autowired
  CouponService couponService;
  @Autowired
  CartRedisRepository cartRedisRepository;

  @Autowired
  CouponRepository couponRepository;
  private Coupon coupon;

  @BeforeEach
  void setup() {
    this.coupon = new Coupon();
    this.coupon.setName("10000원 쿠폰");
    this.coupon.setDeductedPrice(10000);
    this.coupon.setSerialNumber("11111111");
  }

  @Test
  @DisplayName("유효한 쿠폰 테스트")
  void validCoupon() {
    coupon.setExpiredDate(LocalDate.now().plusDays(7));
    coupon.setCouponStatus(CouponStatus.NOT_USED);
    couponRepository.save(coupon);

    String result = couponService.validateCoupon(coupon.getSerialNumber());

    assertEquals("유효한 쿠폰입니다.", result);
  }

  @Test
  @DisplayName("만료된 쿠폰 테스트")
  void expiredCoupon() {
    coupon.setExpiredDate(LocalDate.now().minusDays(7));
    coupon.setCouponStatus(CouponStatus.NOT_USED);
    couponRepository.save(coupon);

    String result = couponService.validateCoupon(coupon.getSerialNumber());

    assertEquals("쿠폰이 만료되었습니다.", result);
  }

  @Test
  @DisplayName("사용한 쿠폰 테스트")
  void usedCoupon() {
    coupon.setExpiredDate(LocalDate.now().plusDays(7));
    coupon.setCouponStatus(CouponStatus.USED);
    couponRepository.save(coupon);

    String result = couponService.validateCoupon(coupon.getSerialNumber());

    assertEquals("이미 사용된 쿠폰입니다.", result);
  }


  @Test
  @DisplayName("쿠폰 사용 상태 업데이트 테스트")
  void useCoupon() {
    coupon.setExpiredDate(LocalDate.now().plusDays(7));
    coupon.setCouponStatus(CouponStatus.NOT_USED);
    couponRepository.save(coupon);

    couponService.updateCouponStatusToUsed(coupon.getId());


    Coupon updatedCoupon = couponRepository.findById(coupon.getId()).orElse(null);
    System.out.println("updatedCoupon = " + updatedCoupon);
    assertEquals(CouponStatus.USED, updatedCoupon.getCouponStatus());
  }
}
