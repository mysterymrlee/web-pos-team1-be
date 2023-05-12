package com.ssg.webpos.service;

import com.ssg.webpos.domain.Delivery;
import com.ssg.webpos.domain.enums.DeliveryType;
import com.ssg.webpos.dto.DeliveryAddDTO;
import com.ssg.webpos.repository.CartRedisImplRepository;
import com.ssg.webpos.repository.UserRepository;
import com.ssg.webpos.repository.delivery.DeliveryRedisImplRepository;
import com.ssg.webpos.repository.delivery.DeliveryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        .requestFinishedAt("2023-05-12T12:34:56")
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
  @Transactional
  @DisplayName("User 배송지 목록 조회")
  void getUserDeliveryListTest() throws Exception {
//		Map<String, Map<String, List<Object>>> all = cartRedisImplRepository.findAll();
//		// 포인트 redis에서 전화번호 찾기
//		List<String> allPhoneNumbers = cartRedisImplRepository.findAllPhoneNumbers();
//		System.out.println("allPhoneNumbers = " + allPhoneNumbers);
//		String findPhoneNumber = allPhoneNumbers.get(0);
//		System.out.println("findPhoneNumber = " + findPhoneNumber);
//		System.out.println("all = " + all);
//
//		// redis로 userId룰 유지해가지고
//
//
//		// 회원 찾기
//		User user = userRepository.findByPhoneNumber(findPhoneNumber).get();
//		List<DeliveryAddress> deliveryAddressList = user.getDeliveryAddressList();
//		for (DeliveryAddress deliveryAddress : deliveryAddressList) {
//			deliveryAddress.getAddress();
//		}
//
//		System.out.println("user = " + user);
//
//		List<DeliveryDTO> deliveryDTOList = new ArrayList<>();
//
//		DeliveryDTO deliveryDTO1 = DeliveryDTO.builder()
//				.deliveryName("집")
//				.userName("김진아")
//				.phoneNumber("01011113333")
//				.address("부산광역시 부산진구")
//				.build();
//
//		DeliveryDTO deliveryDTO2 = DeliveryDTO.builder()
//				.deliveryName("스파로스")
//				.userName("김진아")
//				.phoneNumber("01011113333")
//				.address("부산광역시 해운대구")
//				.build();

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

}
