package com.ssg.webpos.repository;

import com.ssg.webpos.domain.PosStoreCompositeId;
import com.ssg.webpos.dto.DeliveryDTO;
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
    DeliveryDTO deliveryDTO1 = new DeliveryDTO();
    deliveryDTO1.setPosStoreCompositeId(posStoreCompositeId);
    deliveryDTO1.setDeliveryName("회사");
    deliveryDTO1.setUserName("김진아");
    deliveryDTO1.setPhoneNumber("01011117777");
    deliveryDTO1.setAddress("부산광역시 부산진구");

    DeliveryDTO deliveryDTO2 = new DeliveryDTO();
    deliveryDTO2.setPosStoreCompositeId(posStoreCompositeId);
    deliveryDTO2.setDeliveryName("집");
    deliveryDTO2.setUserName("김진아");
    deliveryDTO2.setPhoneNumber("01011117777");
    deliveryDTO2.setAddress("부산광역시 해운대구");

    DeliveryDTO deliveryDTO3 = new DeliveryDTO();
    deliveryDTO3.setPosStoreCompositeId(posStoreCompositeId);
    deliveryDTO3.setDeliveryName("학교");
    deliveryDTO3.setUserName("김진아");
    deliveryDTO3.setPhoneNumber("01011117777");
    deliveryDTO3.setAddress("부산광역시 동래구");

    System.out.println("deliveryDTO1 = " + deliveryDTO1);
    System.out.println("deliveryDTO2 = " + deliveryDTO2);
    System.out.println("deliveryDTO3 = " + deliveryDTO3);
    // when
    deliveryRedisImplRepository.save(deliveryDTO1);
    deliveryRedisImplRepository.save(deliveryDTO2);
    deliveryRedisImplRepository.save(deliveryDTO3);

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

