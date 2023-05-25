package com.ssg.webpos.repository;

import com.ssg.webpos.domain.PosStoreCompositeId;
import com.ssg.webpos.dto.delivery.*;
import com.ssg.webpos.dto.gift.GiftDTO;
import com.ssg.webpos.dto.gift.GiftRequestDTO;
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

    DeliveryRedisAddRequestDTO deliveryAddRequestDTO = new DeliveryRedisAddRequestDTO();
    deliveryAddRequestDTO.setPosId(posStoreCompositeId.getPos_id());
    deliveryAddRequestDTO.setStoreId(posStoreCompositeId.getStore_id());

    List<DeliveryRedisAddDTO> deliveryAddList = new ArrayList<>();
    DeliveryRedisAddDTO deliveryAddDTO = DeliveryRedisAddDTO.builder()
        .deliveryName("집")
        .userName("김진아")
        .address("부산광역시 부산진구")
        .phoneNumber("01087654321")
        .requestInfo("문 앞에 두고 가세요.")
        .requestDeliveryTime("14:00~16:00")
        .build();
    deliveryAddList.add(deliveryAddDTO);
    deliveryAddRequestDTO.setDeliveryAddList(deliveryAddList);

    deliveryRedisImplRepository.saveDelivery(deliveryAddRequestDTO);
    Map<String, Map<String, List<Object>>> findDelivery = deliveryRedisImplRepository.findAll();
    System.out.println("findDelivery = " + findDelivery);
  }

  @Test
  @DisplayName("회원 배송지 목록에서 선택된 배송지 redis에 저장")
  public void saveSelectedDeliveryInfoFromRedis() throws Exception {
    PosStoreCompositeId posStoreCompositeId = new PosStoreCompositeId();
    posStoreCompositeId.setPos_id(1L);
    posStoreCompositeId.setStore_id(1L);

    DeliveryListRedisSelectRequestDTO deliveryListRedisSelectRequestDTO = new DeliveryListRedisSelectRequestDTO();
    deliveryListRedisSelectRequestDTO.setPosId(posStoreCompositeId.getPos_id());
    deliveryListRedisSelectRequestDTO.setStoreId(posStoreCompositeId.getStore_id());
    //given
    List<DeliveryListRedisSelectDTO> selectedDeliveryAddress = new ArrayList<>();
    DeliveryListRedisSelectDTO deliveryListRedisSelectDTO = DeliveryListRedisSelectDTO.builder()
        .deliveryName("집")
        .userName("김진아")
        .address("부산광역시 부산진구")
        .postCode("48119")
        .isDefault(true)
        .requestDeliveryTime("12:00~15:00")
        .requestInfo("문 앞에 놔두고 가세요.")
        .build();
    selectedDeliveryAddress.add(deliveryListRedisSelectDTO);
    deliveryListRedisSelectRequestDTO.setSelectedDeliveryAddress(selectedDeliveryAddress);
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

    GiftRequestDTO giftRequestDTO = new GiftRequestDTO();
    giftRequestDTO.setPosId(posStoreCompositeId.getPos_id());
    giftRequestDTO.setStoreId(posStoreCompositeId.getStore_id());

    List<GiftDTO> giftInfoList = new ArrayList<>();
    GiftDTO giftDTO = new GiftDTO();
    giftDTO.setName("홍길동");
    giftDTO.setPhoneNumber("01011112222");
    giftInfoList.add(giftDTO);

    giftRequestDTO.setGiftRecipientInfo(giftInfoList);

    deliveryRedisImplRepository.saveGiftRecipientInfo(giftRequestDTO);
    Map<String, Map<String, List<Object>>> findGiftInfo = deliveryRedisImplRepository.findAll();
    System.out.println("findGiftInfo = " + findGiftInfo);
  }

  @Test
  void deleteAll() throws Exception {
    deliveryRedisImplRepository.deleteAll();

    Map<String, Map<String, List<Object>>> allAfterDeletion = deliveryRedisImplRepository.findAll();
    System.out.println("After deletion: " + allAfterDeletion);

    Assertions.assertTrue(allAfterDeletion.isEmpty());
  }
}