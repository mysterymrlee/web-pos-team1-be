package com.ssg.webpos.repository;

import com.ssg.webpos.domain.enums.DeliveryType;
import com.ssg.webpos.dto.delivery.DeliveryAddDTO;
import com.ssg.webpos.dto.delivery.DeliveryAddressDTO;
import com.ssg.webpos.repository.delivery.DeliveryRedisImplRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
  @DisplayName("posId로 해당 배송 정보 redis 정보 가져오기")
  public void readDeliveryInfoFromRedisWithPosId() throws Exception {
    DeliveryAddDTO deliveryAddDTO = new DeliveryAddDTO();
    deliveryAddDTO.setStoreId(1L);
    deliveryAddDTO.setPosId(1L);
    //given
    DeliveryAddDTO deliveryDTO1 = DeliveryAddDTO.builder()
        .posId(deliveryAddDTO.getPosId())
        .storeId(deliveryAddDTO.getStoreId())
        .deliveryName("home")
        .userName("김진아4")
        .address("부산광역시 부산진구4")
        .phoneNumber("01011113333")
        .requestFinishedAt("2023-05-12T12:34:56")
        .requestInfo("문 앞에 두고 가세요.")
        .deliveryType(DeliveryType.DELIVERY)
        .build();

    System.out.println("deliveryDTO1 = " + deliveryDTO1);
    // when
    deliveryRedisImplRepository.saveDelivery(deliveryDTO1);

    Map<String, Map<String, List<Object>>> findDeliveryRedisImpl = deliveryRedisImplRepository.findAll();
    System.out.println("deliveryDTOList = " + findDeliveryRedisImpl);

    Assertions.assertEquals(1, findDeliveryRedisImpl.size());
  }

  @Test
  @DisplayName("선택된 배송 정보 redis에 저장")
  public void readSelectedDeliveryInfoFromRedis() throws Exception {
    DeliveryAddressDTO deliveryAddressDTO = new DeliveryAddressDTO();
    deliveryAddressDTO.setStoreId(1L);
    deliveryAddressDTO.setPosId(1L);
    //given
    DeliveryAddressDTO deliveryAddressDTO1 = DeliveryAddressDTO.builder()
        .posId(deliveryAddressDTO.getPosId())
        .storeId(deliveryAddressDTO.getStoreId())
        .address("부산광역시 해운대구")
        .phoneNumber("01011113333")
        .name("김진아")
        .postCode("123456")
        .isDefault(false)
        .deliveryName("스파로스")
        .build();

    System.out.println("deliveryAddressDTO1 = " + deliveryAddressDTO1);
    // when
    deliveryRedisImplRepository.saveSelectedDelivery(deliveryAddressDTO1);

    Map<String, Map<String, List<Object>>> findDeliveryRedisImpl = deliveryRedisImplRepository.findAll();
    System.out.println("deliveryAddressDTO = " + findDeliveryRedisImpl);

    Assertions.assertEquals(1, findDeliveryRedisImpl.size());
  }

  @Test
  void deleteAll() throws Exception {
    deliveryRedisImplRepository.deleteAll();

    Map<String, Map<String, List<Object>>> allAfterDeletion = deliveryRedisImplRepository.findAll();
    System.out.println("After deletion: " + allAfterDeletion);

    Assertions.assertTrue(allAfterDeletion.isEmpty());
  }
}