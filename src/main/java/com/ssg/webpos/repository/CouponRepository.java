package com.ssg.webpos.repository;

import com.ssg.webpos.domain.Coupon;
import com.ssg.webpos.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
  Optional<Coupon> findBySerialNumber(String serialNumber);
}
