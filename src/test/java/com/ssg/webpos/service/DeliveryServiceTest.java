package com.ssg.webpos.service;

import com.ssg.webpos.domain.*;
import com.ssg.webpos.domain.enums.DeliveryStatus;
import com.ssg.webpos.domain.enums.DeliveryType;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.domain.enums.PayMethod;
import com.ssg.webpos.dto.PaymentsDTO;
import com.ssg.webpos.dto.delivery.DeliveryAddDTO;
import com.ssg.webpos.dto.delivery.DeliveryAddressDTO;
import com.ssg.webpos.dto.delivery.DeliveryListRedisSelectRequestDTO;
import com.ssg.webpos.dto.delivery.DeliveryRedisAddRequestDTO;
import com.ssg.webpos.dto.gift.GiftRequestDTO;
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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
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

  @Test
  public void testLocalDateParse() {
    String requestFinishedAt = "2023-05-25T17:25:00";
    try {
      LocalDateTime result = deliveryService.LocalDateParse(requestFinishedAt);
      LocalDateTime expectedDateTime = LocalDateTime.parse(requestFinishedAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
      Assertions.assertEquals(expectedDateTime, result, "LocalDateTime이 예상과 다릅니다.");
    } catch (DateTimeParseException e) {
      Assertions.fail("올바른 형식의 날짜와 시간이 아닙니다.");
    }
  }
  @Test
  public void testMakeSerialNumber() {
    String result = deliveryService.makeSerialNumber();
    System.out.println("result = " + result);

  }


  @Test
  public void testSaveSelectedDelivery() throws Exception {
    Long storeId = 1L;
    Long posId = 2L;
    String compositeId = storeId + "-" + posId;
    PosStoreCompositeId posStoreCompositeId = new PosStoreCompositeId();
    posStoreCompositeId.setStore_id(1L);
    posStoreCompositeId.setPos_id(2L);

    //given
    deliveryRedisImplRepository.delete(compositeId);
    List<DeliveryListRedisSelectRequestDTO> selectedDeliveryAddress = new ArrayList<>();
    DeliveryListRedisSelectRequestDTO deliveryListRedisSelectRequestDTO = DeliveryListRedisSelectRequestDTO.builder()
        .posId(posStoreCompositeId.getStore_id())
        .storeId(posStoreCompositeId.getPos_id())
        .deliveryName("집")
        .userName("김진아")
        .address("부산광역시 부산진구")
        .postCode("48119")
        .isDefault((byte) 1)
        .requestDeliveryTime("12:00~15:00")
        .requestInfo("문 앞에 놔두고 가세요.")
        .phoneNumber("01032244099")
        .build();
    selectedDeliveryAddress.add(deliveryListRedisSelectRequestDTO);
    System.out.println("deliveryListRedisSelectRequestDTO = " + deliveryListRedisSelectRequestDTO);
    System.out.println("selectedDeliveryAddress = " + selectedDeliveryAddress);
    // when
    deliveryRedisImplRepository.saveSelectedDelivery(deliveryListRedisSelectRequestDTO);
    Map<String, Map<String, List<Object>>> deliveryfindall = deliveryRedisImplRepository.findAll();
    System.out.println(deliveryfindall);
    deliveryService.saveSelectedDelivery(createMockPaymentsDTO());
  }

  @Test
  public void testSaveGiftInfo() throws Exception {
    Long storeId = 1L;
    Long posId = 2L;
    String compositeId = storeId + "-" + posId;
    PosStoreCompositeId posStoreCompositeId = new PosStoreCompositeId();
    posStoreCompositeId.setStore_id(1L);
    posStoreCompositeId.setPos_id(2L);

    //given
    deliveryRedisImplRepository.delete(compositeId);

    List<GiftRequestDTO> giftInfoList = new ArrayList<>();
    GiftRequestDTO giftRequestDTO = GiftRequestDTO.builder()
        .posId(posStoreCompositeId.getPos_id())
        .storeId(posStoreCompositeId.getStore_id())
        .receiver("김진아")
        .sender("홍길순")
        .phoneNumber("01011113333")
        .build();
    giftInfoList.add(giftRequestDTO);
    System.out.println("giftRequestDTO = " + giftRequestDTO);
    deliveryRedisImplRepository.saveGiftRecipientInfo(giftRequestDTO);
    Map<String, Map<String, List<Object>>> findGiftInfo = deliveryRedisImplRepository.findAll();
    System.out.println("findGiftInfo = " + findGiftInfo);
    deliveryService.saveGiftInfo(createMockPaymentsDTO());
  }

  @Test
  public void testSaveAddedDelivery() throws Exception {
    Long storeId = 1L;
    Long posId = 2L;
    String compositeId = storeId + "-" + posId;
    deliveryRedisImplRepository.delete(compositeId);
//    List<DeliveryRedisAddRequestDTO> deliveryRedisAddRequestDTOList = new ArrayList<>();
    DeliveryRedisAddRequestDTO deliveryRedisAddRequestDTO = DeliveryRedisAddRequestDTO.builder()
        .storeId(1L)
        .posId(2L)
        .deliveryName("우리집")
        .userName("김진아")
        .phoneNumber("01032244099")
        .address("부산광역시 남구")
        .requestDeliveryTime("12:00~15:00")
        .postCode("052508")
        .requestInfo("부재 시, 경비실에 맡겨주세요.")
        .build();
//    deliveryRedisAddRequestDTOList.add(deliveryRedisAddRequestDTO);
    // when
    deliveryRedisImplRepository.saveDelivery(deliveryRedisAddRequestDTO);
    Map<String, Map<String, List<Object>>> deliveryfindall = deliveryRedisImplRepository.findAll();
    System.out.println(deliveryfindall);
    deliveryService.saveAddedDelivery(createMockPaymentsDTO());
  }

  @Test
  public void addUserDeliveryAddress() throws Exception {
    DeliveryRedisAddRequestDTO deliveryRedisAddRequestDTO = DeliveryRedisAddRequestDTO.builder()
        .storeId(1L)
        .posId(2L)
        .deliveryName("우리집")
        .userName("김진아")
        .phoneNumber("01032244099")
        .address("부산광역시 남구")
        .requestDeliveryTime("12:00~15:00")
        .postCode("052508")
        .requestInfo("부재 시, 경비실에 맡겨주세요.")
        .build();
    // when
    deliveryService.addUserDeliveryAddress(deliveryRedisAddRequestDTO);
  }

  private PaymentsDTO createMockPaymentsDTO() {
    PaymentsDTO paymentsDTO = new PaymentsDTO();
    paymentsDTO.setStoreId(1L);
    paymentsDTO.setPosId(2L);

    return paymentsDTO;
  }

  private Order createOrder() {
    // Order를 생성하는 로직
    Order order = Order.builder()
        .orderStatus(OrderStatus.SUCCESS)
        .payMethod(PayMethod.CREDIT_CARD)
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
        .phoneNumber("01032244099")
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