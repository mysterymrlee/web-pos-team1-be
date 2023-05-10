package com.ssg.webpos.repository;

import com.ssg.webpos.domain.Coupon;
import com.ssg.webpos.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
  Coupon findBySerialNumber(String serialNumber);
}
