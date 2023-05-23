package com.ssg.webpos.repository.delivery;

import com.ssg.webpos.domain.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
  Delivery findBySerialNumber(String serialNumber);
}
