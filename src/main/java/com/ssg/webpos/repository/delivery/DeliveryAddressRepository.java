package com.ssg.webpos.repository.delivery;

import com.ssg.webpos.domain.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {
  List<DeliveryAddress> findByUserId(Long userId);
}
