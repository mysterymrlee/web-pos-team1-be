package com.ssg.webpos.service;

import com.ssg.webpos.domain.Delivery;
import com.ssg.webpos.domain.DeliveryAddress;
import com.ssg.webpos.domain.enums.DeliveryType;
import com.ssg.webpos.dto.DeliveryAddDTO;
import com.ssg.webpos.repository.CartRedisImplRepository;
import com.ssg.webpos.repository.UserRepository;
import com.ssg.webpos.repository.delivery.DeliveryRedisImplRepository;
import com.ssg.webpos.repository.delivery.DeliveryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DeliveryServiceTest {
  @Autowired
  DeliveryRepository deliveryRepository;
  @Autowired
  UserRepository userRepository;
  @Autowired
  DeliveryRedisImplRepository deliveryRedisImplRepository;
  @Autowired
  CartRedisImplRepository cartRedisImplRepository;
  @Autowired
  DeliveryService deliveryService;

  @Test
  @DisplayName("배송지 추가")
  void addDeliveryAddressTest() {
    DeliveryAddDTO deliveryDTO = DeliveryAddDTO.builder()
        .deliveryName("home")
        .userName("김진아")
        .address("부산광역시 부산진구")
        .phoneNumber("01011113333")
        .requestFinishedAt("18:00")
        .requestInfo("문 앞에 두고 가세요.")
        .deliveryType(DeliveryType.DELIVERY)
        .build();
    deliveryService.addDeliveryAddress(deliveryDTO);

    List<Delivery> deliveryList = deliveryRepository.findAll();
    for (Delivery delivery : deliveryList) {
      System.out.println("delivery = " + delivery);
    }
//		assertEquals(1, deliveryList.size());
  }

  @Test
  void getDeliveryListTest() {
    List<Delivery> deliveryList = deliveryRepository.findAll();
    for (Delivery delivery : deliveryList) {
      System.out.println("delivery = " + delivery);
    }
  }

  @Test
  void updateDeliveryInfoTest() {
    Long deliveryId = 1L;
    Delivery delivery = deliveryRepository.findById(1L).get();


  }

  @Test
  void deleteDeliveryInfoTest() {

  }

  @Test
  @DisplayName("배송 시간 입력 포맷 검증 에러")
  void addressFormat() {
    Assertions.assertThrows(DateTimeParseException.class, () -> {
      deliveryService.LocaldateParse("2023-05-12T18:00:00");
    });
  }
}
