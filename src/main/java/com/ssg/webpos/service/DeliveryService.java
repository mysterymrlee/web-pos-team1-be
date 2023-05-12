package com.ssg.webpos.service;

import com.ssg.webpos.domain.Delivery;
import com.ssg.webpos.domain.enums.DeliveryStatus;
import com.ssg.webpos.domain.enums.DeliveryType;
import com.ssg.webpos.dto.DeliveryAddDTO;
import com.ssg.webpos.repository.delivery.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
public class DeliveryService {
  private final DeliveryRepository deliveryRepository;
  // 문자열을 LocalDateTime으로 파싱
  public LocalDateTime LocaldateParse(String requestFinishedAt) throws DateTimeParseException {
    LocalDateTime dateTime = LocalDateTime.parse(requestFinishedAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    System.out.println("LocalDateTime = " + dateTime); // 2023-05-12T18:00:00
    return dateTime;
  }

  @Transactional
  public void addDeliveryAddress(DeliveryAddDTO deliveryDTO) {
    LocalDateTime requestFinishedAt = LocaldateParse(deliveryDTO.getRequestFinishedAt());
    Delivery delivery = Delivery.builder()
        .deliveryName(deliveryDTO.getDeliveryName())
        .userName(deliveryDTO.getUserName())
        .address(deliveryDTO.getAddress())
        .phoneNumber(deliveryDTO.getPhoneNumber())
        .finishedDate(requestFinishedAt)
        .requestInfo(deliveryDTO.getRequestInfo())
        .deliveryStatus(DeliveryStatus.PROCESS)
        .deliveryType(deliveryDTO.getDeliveryType())
        .startedDate(LocalDateTime.now())
        .build();


    deliveryRepository.save(delivery);
  }
}
