package com.ssg.webpos.repository;

import com.ssg.webpos.domain.Coupon;
import com.ssg.webpos.domain.enums.CouponStatus;
import com.ssg.webpos.dto.CouponDTO;
import com.ssg.webpos.repository.order.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class CouponRepositoryTest {
  @Autowired
  CouponRepository couponRepository;

  @Autowired
  OrderRepository orderRepository;
  
  @Test
  void 쿠폰_차감(){




  }
  
}