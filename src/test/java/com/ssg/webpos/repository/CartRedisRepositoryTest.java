package com.ssg.webpos.repository;

import com.ssg.webpos.domain.Cart;
import com.ssg.webpos.domain.PosStoreCompositeId;
import com.ssg.webpos.dto.CartAddDTO;
import com.ssg.webpos.dto.PointDTO;
import com.ssg.webpos.service.CartRedisService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Rollback(value = false)
public class CartRedisRepositoryTest {
  @Autowired
  CartRedisImplRepository cartRedisRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  CartRedisService cartRedisService;

    @Test
    void readRedis() throws Exception {
      Map<String, Map<String, List<Object>>> redis = cartRedisRepository.findAll();
      System.out.println("redis = " + redis);
    }

    @Test
    void saveCartToDBTest() throws Exception {
      PosStoreCompositeId posStoreCompositeId = new PosStoreCompositeId();
      posStoreCompositeId.setPos_id(1L);
      posStoreCompositeId.setStore_id(3L);

      CartAddDTO cartAddDTO1 = new CartAddDTO();
      cartAddDTO1.setPosStoreCompositeId(posStoreCompositeId);
      cartAddDTO1.setTotalPrice(10000);
      cartAddDTO1.setProductId(1L);
      cartAddDTO1.setCartQty(10);

      CartAddDTO cartAddDTO2 = new CartAddDTO();
      cartAddDTO2.setPosStoreCompositeId(posStoreCompositeId);
      cartAddDTO2.setTotalPrice(10000);
      cartAddDTO2.setProductId(2L);
      cartAddDTO2.setCartQty(5);

      cartRedisRepository.saveCart(cartAddDTO1);
      cartRedisRepository.saveCart(cartAddDTO2);
      Map<String, Map<String, List<Object>>> all = cartRedisRepository.findAll();

      System.out.println("all = " + all);
  
      String compositeId = posStoreCompositeId.getStore_id() + "-" + posStoreCompositeId.getPos_id();
      System.out.println("compositeId = " + compositeId);
      cartRedisService.saveCartToDB(compositeId);
      

    }
    
    @Test
    void readPhoneNumber() throws Exception {
      PointDTO pointDTO = new PointDTO();
      pointDTO.setPhoneNumber("01011113333");
      pointDTO.setPointMethod("phoneNumber");
      pointDTO.setStoreId(3L);
      pointDTO.setPosId(1L);
      cartRedisRepository.savePoint(pointDTO);

      Map<String, Map<String, List<Object>>> all = cartRedisRepository.findAll();
      List<String> phoneNumbersByCompositeId = cartRedisRepository.findPhoneNumbersByCompositeId(pointDTO.getStoreId() + "-" + pointDTO.getPosId());
      System.out.println("phoneNumbersByCompositeId = " + phoneNumbersByCompositeId);

      System.out.println("all = " + all);

    }

  @Test
  void findUserId() throws Exception {
    PointDTO pointDTO = new PointDTO();
    pointDTO.setPhoneNumber("01011113333");
    pointDTO.setPointMethod("phoneNumber");
    pointDTO.setStoreId(3L);
    pointDTO.setPosId(1L);
    cartRedisRepository.savePoint(pointDTO);

    Map<String, Map<String, List<Object>>> all = cartRedisRepository.findAll();
    Long userId = cartRedisRepository.findUserId(pointDTO.getStoreId() + "-" + pointDTO.getPosId());
    System.out.println("userId = " + userId);

    System.out.println("all = " + all);

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

      CartAddDTO cartAddDTO2 = new CartAddDTO();
      cartAddDTO2.setPosStoreCompositeId(posStoreCompositeId);
      cartAddDTO2.setTotalPrice(10000);
      cartAddDTO2.setProductId(2L);
      cartAddDTO2.setCartQty(5);

      CartAddDTO cartAddDTO3 = new CartAddDTO();
      cartAddDTO3.setPosStoreCompositeId(posStoreCompositeId);
      cartAddDTO3.setProductId(1L);
      cartAddDTO3.setCartQty(5);


      cartRedisRepository.saveCart(cartAddDTO1);
      cartRedisRepository.saveCart(cartAddDTO2);
      cartRedisRepository.saveCart(cartAddDTO3);
      Map<String, Map<String, List<Object>>> cartall = cartRedisRepository.findAll();
      System.out.println("cartall = " + cartall);

    }

    @Test
    @DisplayName("포인트 redis 저장")
    public void readPointInfoFromRedisAll () throws Exception {

      PointDTO pointDTO = new PointDTO();
      pointDTO.setPhoneNumber("01011112222");
      pointDTO.setPointMethod("phoneNumber");
      pointDTO.setStoreId(3L);
      pointDTO.setPosId(1L);
      cartRedisRepository.savePoint(pointDTO);

      Map<String, Map<String, List<Object>>> all = cartRedisRepository.findAll();
      System.out.println("all = " + all);
//      Map<String, List<Object>> byId = cartRedisRepository.findById(String.valueOf(pointDTO.getPosStoreCompositeId()));
//
//      Assertions.assertEquals(2, byId.size());


    }


  @Test
  @DisplayName("해당 posId 삭제")
  public void deleteFromRedisWithPosId() throws Exception {
    PosStoreCompositeId posStoreCompositeId = new PosStoreCompositeId();
    posStoreCompositeId.setPos_id(2L);
    posStoreCompositeId.setStore_id(3L);

    // given
    CartAddDTO cartAddDTO1 = new CartAddDTO();
    cartAddDTO1.setPosStoreCompositeId(posStoreCompositeId);
    cartAddDTO1.setProductId(2L);
    cartAddDTO1.setCartQty(3);

    cartRedisRepository.saveCart(cartAddDTO1);

    String compositeId = posStoreCompositeId.getPos_id() + "-" + posStoreCompositeId.getStore_id();

    // when
    cartRedisRepository.delete(compositeId);

    Map<String, Map<String, List<Object>>> allAfterDeletion = cartRedisRepository.findAll();
    System.out.println("allAfterDeletion = " + allAfterDeletion);

    // then
    Assertions.assertFalse(allAfterDeletion.containsKey(compositeId));
  }



  @Test
  void deleteAll() throws Exception {
    cartRedisRepository.deleteAll();

    Map<String, Map<String, List<Object>>> allAfterDeletion = cartRedisRepository.findAll();
    System.out.println("After deletion: " + allAfterDeletion);

    Assertions.assertTrue(allAfterDeletion.isEmpty());
  }
}

