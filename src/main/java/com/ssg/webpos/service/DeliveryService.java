package com.ssg.webpos.service;

import com.ssg.webpos.domain.Delivery;
import com.ssg.webpos.domain.enums.DeliveryStatus;
import com.ssg.webpos.domain.enums.DeliveryType;
import com.ssg.webpos.dto.DeliveryDTO;
import com.ssg.webpos.repository.delivery.DeliveryRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeliveryService {
  private final DeliveryRepository deliveryRepository;
  private final OrderRepository orderRepository;
  @Transactional
  public void addDeliveryAddress(DeliveryDTO deliveryDTO) {
    Delivery delivery = new Delivery();
    delivery.setDeliveryName(deliveryDTO.getDeliveryName());
    delivery.setUserName(deliveryDTO.getUserName());
    delivery.setAddress(deliveryDTO.getAddress());
    delivery.setPhoneNumber(deliveryDTO.getPhoneNumber());
    delivery.setDeliveryStatus(DeliveryStatus.PROCESS);
    delivery.setDeliveryType(DeliveryType.DELIVERY);
    delivery.setStartedDate(LocalDateTime.now());

    deliveryRepository.save(delivery);
  }
}
