package com.ssg.webpos.service;

import com.ssg.webpos.domain.*;
import com.ssg.webpos.domain.enums.DeliveryType;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.domain.enums.PayMethod;
import com.ssg.webpos.dto.delivery.DeliveryAddDTO;
import com.ssg.webpos.dto.delivery.DeliveryAddressDTO;
import com.ssg.webpos.repository.UserRepository;
import com.ssg.webpos.repository.delivery.DeliveryAddressRepository;
import com.ssg.webpos.repository.delivery.DeliveryRedisImplRepository;
import com.ssg.webpos.repository.delivery.DeliveryRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

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
  DeliveryAddressRepository deliveryAddressRepository;
  @Autowired
  DeliveryService deliveryService;
  @Autowired
  OrderRepository orderRepository;
  @Autowired
  OrderService orderService;

  private Order createOrder() {
    // Order를 생성하는 로직
    Order order = Order.builder()
        .orderStatus(OrderStatus.SUCCESS)
        .payMethod(PayMethod.CREDIT_CARD)
        .totalQuantity(4)
        .orderDate(LocalDateTime.now())
        .build();
    Order savedOrder = orderRepository.save(order);
    return savedOrder;
  }

  @Test
  @DisplayName("배송지 추가")
  void addDeliveryAddressTest() {
    Order createdOrder = createOrder();

    DeliveryAddDTO deliveryDTO = DeliveryAddDTO.builder()
        .deliveryName("집")
        .userName("김진아")
        .address("부산광역시 해운대구")
        .phoneNumber("01012345678")
        .requestInfo("초인종 누르지 말아주세요.")
        .deliveryType(DeliveryType.DELIVERY)
        .requestDeliveryTime("15:00~18:00")
        .postCode("48060")
        .build();
    deliveryService.addDeliveryAddress(deliveryDTO, createdOrder.getId());

    List<Delivery> deliveryList = deliveryRepository.findAll();
    for (Delivery delivery : deliveryList) {
      System.out.println("delivery = " + delivery);
    }
//    assertEquals(2, deliveryList.size());
  }

  @Test
  @Transactional
  @DisplayName("User 배송지 목록 조회")
  void getUserDeliveryListTest() throws Exception {
    List<DeliveryAddressDTO> userAllDeliveryList = deliveryService.getUserAllDeliveryList();
    System.out.println("userAllDeliveryList = " + userAllDeliveryList);

    assertEquals(2, userAllDeliveryList.size());
  }

  @Test
  @Transactional
  @DisplayName("배송지 목록 조회")
  void getDeliveryListTest() {
    List<DeliveryAddress> deliveryList = deliveryAddressRepository.findAll();
    for (DeliveryAddress delivery : deliveryList) {
      System.out.println("delivery = " + delivery);
    }
  }

  @Test
  @DisplayName("배송 시간 입력 포맷 검증 에러")
  void addressFormat() {
    Assertions.assertThrows(DateTimeParseException.class, () -> {
      deliveryService.LocalDateParse("10:00");
    });
  }
}