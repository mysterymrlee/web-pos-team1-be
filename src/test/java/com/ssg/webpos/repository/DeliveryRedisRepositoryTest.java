package com.ssg.webpos.repository;

import com.ssg.webpos.domain.PosStoreCompositeId;
import com.ssg.webpos.domain.enums.DeliveryType;
import com.ssg.webpos.dto.DeliveryAddDTO;
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
  @DisplayName("posId로 해당 배송 정보 redis 정보 가져오기")
  public void readDeliveryInfoFromRedisWithPosId() throws Exception {
    PosStoreCompositeId posStoreCompositeId = new PosStoreCompositeId();
    posStoreCompositeId.setPos_id(1L);
    posStoreCompositeId.setStore_id(1L);
    System.out.println("posStoreCompositeId = " + posStoreCompositeId);
    //given
    DeliveryAddDTO deliveryDTO1 = DeliveryAddDTO.builder()
        .posStoreCompositeId(posStoreCompositeId)
        .deliveryName("home")
        .userName("김진아")
        .address("부산광역시 부산진구")
        .phoneNumber("01011113333")
        .requestFinishedAt("2023-05-12T12:34:56")
        .requestInfo("문 앞에 두고 가세요.")
        .deliveryType(DeliveryType.DELIVERY)
        .build();

    System.out.println("deliveryDTO1 = " + deliveryDTO1);
    // when
    deliveryRedisImplRepository.save(deliveryDTO1);

    Map<String, Map<String, List<Object>>> findDeliveryRedisImpl = deliveryRedisImplRepository.findAll();
    System.out.println("deliveryDTOList = " + findDeliveryRedisImpl);

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