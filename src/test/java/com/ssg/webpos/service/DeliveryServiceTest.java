package com.ssg.webpos.service;
import com.ssg.webpos.domain.*;
import com.ssg.webpos.domain.enums.DeliveryType;
import com.ssg.webpos.dto.delivery.DeliveryAddDTO;
import com.ssg.webpos.dto.delivery.DeliveryAddressDTO;
import com.ssg.webpos.repository.UserRepository;
import com.ssg.webpos.repository.delivery.DeliveryAddressRepository;
import com.ssg.webpos.repository.delivery.DeliveryRedisImplRepository;
import com.ssg.webpos.repository.delivery.DeliveryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import javax.transaction.Transactional;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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

  @Test
  @DisplayName("배송지 추가")
  void addDeliveryAddressTest() {
    DeliveryAddDTO deliveryDTO = DeliveryAddDTO.builder()
        .deliveryName("스파로스")
        .userName("김진아")
        .address("부산광역시 부산진구")
        .phoneNumber("01011113333")
        .requestFinishedAt("2023-05-16T18:00:00")
        .requestInfo("문 앞에 두고 가세요.")
        .deliveryType(DeliveryType.DELIVERY)
        .build();
    deliveryService.addDeliveryAddress(deliveryDTO);

    List<Delivery> deliveryList = deliveryRepository.findAll();
    for (Delivery delivery : deliveryList) {
      System.out.println("delivery = " + delivery);
    }
    assertEquals(1, deliveryList.size());
  }
  @Test
  @Transactional
  @DisplayName("User 배송지 목록 조회")
  void getUserDeliveryListTest() throws Exception {
    // 포인트 redis 저장
    // savePointRedis();
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
  @Transactional
  @DisplayName("유저의 배송지 목록에서 배송할 배송지 선택")
  void selectDeliveryAddressTest() throws Exception {
    long id = 1; // DeliveryList 의 id(pk)
    long userId = 1;
    String address = "부산광역시 해운대구";
    // 배송지 목록 생성
    DeliveryAddress deliveryAddress = DeliveryAddress.builder()
        .address(address)
        .phoneNumber("01011113333")
        .name("김진아")
        .postCode("123456")
        .isDefault(false)
        .deliveryName("스파로스")
        .build();
    DeliveryAddress deliveryAddress1 = deliveryAddressRepository.save(deliveryAddress);
    Long deliveryAddressId = deliveryAddress1.getId();
    DeliveryAddressDTO selectedDeliveryAddress = deliveryService.getSelectedDeliveryAddress(deliveryAddressId);
    System.out.println("selectedDeliveryAddress = " + selectedDeliveryAddress);
    assertEquals(address ,selectedDeliveryAddress.getAddress());

  }

  @Test
  void updateDeliveryInfoTest()  {

    Long deliveryId = 1L;
    Delivery delivery = deliveryRepository.findById(deliveryId).get();
  }

  @Test
  void deleteDeliveryInfoTest() {

  }

  @Test
  @DisplayName("배송 시간 입력 포맷 검증 에러")
  void addressFormat() {
    Assertions.assertThrows(DateTimeParseException.class, () -> {
      deliveryService.LocalDateParse("2023-05-12T18:00:00");
    });
  }

//  void savePointRedis() throws Exception {
//    PosStoreCompositeId posStoreCompositeId = new PosStoreCompositeId();
//    posStoreCompositeId.setPos_id(1L);
//    posStoreCompositeId.setStore_id(1L);
//
//    PointDTO pointDTO = new PointDTO();
//    pointDTO.setPhoneNumber("01011113333");
//    pointDTO.setPointMethod("phoneNumber22");
//    pointDTO.setPosStoreCompositeId(posStoreCompositeId);
//    cartRedisImplRepository.savePoint(pointDTO);
//
//    Map<String, Map<String, List<Object>>> all = cartRedisImplRepository.findAll();
//    System.out.println("all = " + all);
//    Map<String, List<Object>> byId = cartRedisImplRepository.findById(String.valueOf(pointDTO.getPosStoreCompositeId()));
//  }
}