package com.ssg.webpos.repository;

import com.ssg.webpos.domain.PosStoreCompositeId;
import com.ssg.webpos.dto.delivery.*;
import com.ssg.webpos.dto.gift.GiftRequestDTO;
import com.ssg.webpos.repository.cart.CartRedisImplRepository;
import com.ssg.webpos.repository.delivery.DeliveryRedisImplRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class DeliveryRedisRepositoryTest {
  @Autowired
  DeliveryRedisImplRepository deliveryRedisImplRepository;
  @Autowired
  CartRedisImplRepository cartRedisImplRepository;
  @Test
  @DisplayName("redis에 저장된 배송지 목록 전체 조회")
  public void read() throws Exception {
    Map<String, Map<String, List<Object>>> redis = deliveryRedisImplRepository.findAll();
    System.out.println("redis = " + redis);
  }

  @Test
  @DisplayName("배송지 추가한 정보 캐싱하기")
  public void saveDeliveryInfoTest() throws Exception {
    PosStoreCompositeId posStoreCompositeId = new PosStoreCompositeId();
    posStoreCompositeId.setPos_id(1L);
    posStoreCompositeId.setStore_id(1L);

    List<DeliveryRedisAddRequestDTO> deliveryAddList = new ArrayList<>();
    DeliveryRedisAddRequestDTO deliveryRedisAddRequestDTO = DeliveryRedisAddRequestDTO.builder()
        .posId(posStoreCompositeId.getPos_id())
        .storeId(posStoreCompositeId.getStore_id())
        .deliveryName("집")
        .userName("김진아")
        .address("부산광역시 부산진구")
        .phoneNumber("01087654321")
        .requestDeliveryTime("14:00~16:00")
        .postCode("48060")
        .requestInfo("부재 시, 경비실에 맡겨주세요.")
        .build();
    deliveryAddList.add(deliveryRedisAddRequestDTO);
    System.out.println("deliveryAddDTO = " + deliveryRedisAddRequestDTO);
    System.out.println("deliveryAddList = " + deliveryAddList);

    deliveryRedisImplRepository.saveDelivery(deliveryRedisAddRequestDTO);
    Map<String, Map<String, List<Object>>> findDelivery = deliveryRedisImplRepository.findAll();
    System.out.println("findDelivery = " + findDelivery);
  }

  @Test
  @DisplayName("회원 배송지 목록에서 선택된 배송지 redis에 저장")
  public void saveSelectedDeliveryInfoFromRedis() throws Exception {
    PosStoreCompositeId posStoreCompositeId = new PosStoreCompositeId();
    posStoreCompositeId.setPos_id(1L);
    posStoreCompositeId.setStore_id(1L);

    //given
    List<DeliveryListRedisSelectRequestDTO> selectedDeliveryAddress = new ArrayList<>();
    DeliveryListRedisSelectRequestDTO deliveryListRedisSelectRequestDTO = DeliveryListRedisSelectRequestDTO.builder()
        .posId(posStoreCompositeId.getPos_id())
        .storeId(posStoreCompositeId.getStore_id())
        .deliveryName("집")
        .userName("김진아")
        .address("부산광역시 부산진구")
        .postCode("48119")
        .isDefault((byte) 1)
        .requestDeliveryTime("12:00~15:00")
        .requestInfo("문 앞에 놔두고 가세요.")
        .phoneNumber("01049922047")
        .build();
    selectedDeliveryAddress.add(deliveryListRedisSelectRequestDTO);
    System.out.println("deliveryListRedisSelectRequestDTO = " + deliveryListRedisSelectRequestDTO);
    System.out.println("selectedDeliveryAddress = " + selectedDeliveryAddress);
    // when
    deliveryRedisImplRepository.saveSelectedDelivery(deliveryListRedisSelectRequestDTO);

    Map<String, Map<String, List<Object>>> findDeliveryRedisImpl = deliveryRedisImplRepository.findAll();
    System.out.println("findDeliveryRedisImpl = " + findDeliveryRedisImpl);
  }

  @Test
  @DisplayName("선물 받는 사람 정보 캐싱")
  void saveGiftRecipientInfoTest() throws Exception {
    PosStoreCompositeId posStoreCompositeId = new PosStoreCompositeId();
    posStoreCompositeId.setPos_id(1L);
    posStoreCompositeId.setStore_id(1L);

    List<GiftRequestDTO> giftInfoList = new ArrayList<>();
    GiftRequestDTO giftRequestDTO = GiftRequestDTO.builder()
        .posId(posStoreCompositeId.getPos_id())
        .storeId(posStoreCompositeId.getStore_id())
        .receiver("김진아")
        .sender("홍길순")
        .phoneNumber("01011113333")
        .build();
    System.out.println("giftRequestDTO = " + giftRequestDTO);
    deliveryRedisImplRepository.saveGiftRecipientInfo(giftRequestDTO);
    Map<String, Map<String, List<Object>>> findGiftInfo = deliveryRedisImplRepository.findAll();
    System.out.println("findGiftInfo = " + findGiftInfo);
  }

  @Test
  @DisplayName("캐싱된 선물 받는 정보 가져오기")
  void findGiftRecipientInfo() throws Exception {
    GiftRequestDTO giftRequestDTO = GiftRequestDTO.builder()
        .storeId(1L)
        .posId(1L)
        .phoneNumber("01011113333")
        .receiver("김진아")
        .sender("홍길동")
        .build();
    deliveryRedisImplRepository.saveGiftRecipientInfo(giftRequestDTO);
    String compositeId = giftRequestDTO.getStoreId() + "-" + giftRequestDTO.getPosId();

    Map<String, Map<String, List<Object>>> all = deliveryRedisImplRepository.findAll();
    List<Map<String, Object>> giftRecipientInfo = deliveryRedisImplRepository.findGiftRecipientInfo(compositeId);
    System.out.println("findGiftRecipientInfo = " + giftRecipientInfo.get(0));

    System.out.println("all = " + all);
  }

  @Test
  @DisplayName("캐싱된 추가된 배송지 정보 가져오기")
  void findAddedDeliveryAddress() throws Exception {
    DeliveryRedisAddRequestDTO deliveryRedisAddRequestDTO = DeliveryRedisAddRequestDTO.builder()
        .storeId(1L)
        .posId(1L)
        .deliveryName("우리집")
        .userName("김진아")
        .phoneNumber("01011113333")
        .address("부산광역시 남구")
        .requestDeliveryTime("12:00~15:00")
        .postCode("052508")
        .requestInfo("부재 시, 경비실에 맡겨주세요.")
        .build();
    deliveryRedisImplRepository.saveDelivery(deliveryRedisAddRequestDTO);


    List<Map<String, Object>> addedDeliveryAddress = deliveryRedisImplRepository.findAddedDelivery(deliveryRedisAddRequestDTO.getStoreId() + "-" + deliveryRedisAddRequestDTO.getPosId());
    System.out.println("addedDeliveryAddress = " + addedDeliveryAddress.get(0));
  }

  @Test
  void deleteAll() throws Exception {
    deliveryRedisImplRepository.deleteAll();

    Map<String, Map<String, List<Object>>> allAfterDeletion = deliveryRedisImplRepository.findAll();
    System.out.println("After deletion: " + allAfterDeletion);

    Assertions.assertTrue(allAfterDeletion.isEmpty());
  }
}