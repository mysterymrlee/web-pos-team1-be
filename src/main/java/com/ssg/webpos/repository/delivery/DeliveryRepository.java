package com.ssg.webpos.repository.delivery;

import com.ssg.webpos.domain.Delivery;
import com.ssg.webpos.domain.enums.DeliveryStatus;
import com.ssg.webpos.domain.enums.DeliveryType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
  Delivery findBySerialNumber(String serialNumber);
  Delivery findByPhoneNumber(String phoneNumber);
  Delivery findByDeliveryTypeAndPhoneNumber(DeliveryType deliveryType, String phoneNumber);
}
