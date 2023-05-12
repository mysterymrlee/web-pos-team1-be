package com.ssg.webpos.repository;

import com.ssg.webpos.domain.PosStoreCompositeId;
import com.ssg.webpos.dto.CartAddDTO;
import com.ssg.webpos.dto.PointDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class CartRedisRepositoryTest {
  @Autowired
  CartRedisImplRepository cartRedisRepository;

  @Autowired
  UserRepository userRepository;

    @Test
    void readRedis() throws Exception {
      Map<String, Map<String, List<Object>>> redis = cartRedisRepository.findAll();
      System.out.println("redis = " + redis);
      
    }

    @Test
    @DisplayName("카트 redis 저장")
    public void readCartInfoFromRedisWithPosId () throws Exception {

      PosStoreCompositeId posStoreCompositeId = new PosStoreCompositeId();
      posStoreCompositeId.setPos_id(1L);
      posStoreCompositeId.setStore_id(3L);

      CartAddDTO cartAddDTO1 = new CartAddDTO();
      cartAddDTO1.setPosStoreCompositeId(posStoreCompositeId);
      cartAddDTO1.setProductId(5L);
      cartAddDTO1.setCartQty(5);

    cartRedisRepository.saveCart(cartAddDTO1);
      Map<String, Map<String, List<Object>>> cartall = cartRedisRepository.findAll();
      System.out.println("cartall = " + cartall);
      

      Map<String, List<Object>> byId = cartRedisRepository.findById(String.valueOf(cartAddDTO1.getPosStoreCompositeId()));
    System.out.println("cartAddDTOList = " + byId);

    Assertions.assertEquals(1, byId.size());
    }

    @Test
    @DisplayName("포인트 redis 저장")
    public void readPointInfoFromRedisAll () throws Exception {

      PosStoreCompositeId posStoreCompositeId = new PosStoreCompositeId();
      posStoreCompositeId.setPos_id(1L);
      posStoreCompositeId.setStore_id(1L);

      PointDTO pointDTO = new PointDTO();
      pointDTO.setPhoneNumber("01011113333");
      pointDTO.setPointMethod("phoneNumber22");
      pointDTO.setPosStoreCompositeId(posStoreCompositeId);
      cartRedisRepository.savePoint(pointDTO);

      Map<String, Map<String, List<Object>>> all = cartRedisRepository.findAll();
      System.out.println("all = " + all);
      Map<String, List<Object>> byId = cartRedisRepository.findById(String.valueOf(pointDTO.getPosStoreCompositeId()));
     
      Assertions.assertEquals(2, byId.size());


    }


  @Test
  @DisplayName("해당 posId 삭제")
  public void deleteFromRedisWithPosId() throws Exception {
    PosStoreCompositeId posStoreCompositeId = new PosStoreCompositeId();
    String posId = String.valueOf(posStoreCompositeId.getPos_id());
    String storeId = String.valueOf(posStoreCompositeId.getStore_id());
    String compositeId = posId + "-" + storeId;

    // when
    cartRedisRepository.delete(compositeId);

    Map<String, Map<String, List<Object>>> allAfterDeletion = cartRedisRepository.findAll();
    System.out.println("allAfterDeletion = " + allAfterDeletion);

  }



  @Test
  void deleteAll() throws Exception {
    cartRedisRepository.deleteAll();

    Map<String, Map<String, List<Object>>> allAfterDeletion = cartRedisRepository.findAll();
    System.out.println("After deletion: " + allAfterDeletion);

    Assertions.assertTrue(allAfterDeletion.isEmpty());
  }
}

