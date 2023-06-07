package com.ssg.webpos.repository;

import com.ssg.webpos.domain.Coupon;
import com.ssg.webpos.domain.Point;
import com.ssg.webpos.domain.PosStoreCompositeId;
import com.ssg.webpos.domain.User;
import com.ssg.webpos.domain.enums.CouponStatus;
import com.ssg.webpos.domain.enums.RoleUser;
import com.ssg.webpos.dto.cartDto.CartAddDTO;
import com.ssg.webpos.dto.cartDto.CartAddRequestDTO;
import com.ssg.webpos.dto.coupon.CouponAddRequestDTO;
import com.ssg.webpos.dto.point.PointDTO;
import com.ssg.webpos.repository.cart.CartRedisImplRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.testng.AssertJUnit.*;

@SpringBootTest
@Transactional
public class CartRedisRepositoryTest {
  @Autowired
  CartRedisImplRepository cartRedisRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  CouponRepository couponRepository;

    @Test
    void readRedis() throws Exception {
      Map<String, Map<String, List<Object>>> redis = cartRedisRepository.findAll();
      System.out.println("redis = " + redis);
    }

    @Test
    void getDeducted() {
      Coupon coupon = new Coupon();
      coupon.setCouponStatus(CouponStatus.NOT_USED);
      coupon.setName("500원");
      coupon.setSerialNumber("testSerialNumber");
      coupon.setDeductedPrice(500);
      coupon.setExpiredDate(LocalDate.now().plusDays(7));
      couponRepository.save(coupon);

      CouponAddRequestDTO couponAddRequestDTO = new CouponAddRequestDTO();
      couponAddRequestDTO.setPosId(1L);
      couponAddRequestDTO.setStoreId(1L);
      couponAddRequestDTO.setSerialNumber(coupon.getSerialNumber());
      cartRedisRepository.saveCoupon(couponAddRequestDTO);
      String compositeId = couponAddRequestDTO.getStoreId() + "-" + couponAddRequestDTO.getPosId();
      Integer deductedPrice = cartRedisRepository.findDeductedPrice(compositeId);
      System.out.println("deductedPrice = " + deductedPrice);

      assertThat(deductedPrice).isEqualTo(coupon.getDeductedPrice());
    }


    @Test
    @DisplayName("Redis 에 저장된 phoneNumber Get")
    void readPhoneNumber() throws Exception {
      User user = new User();
      user.setName("홍길동1");
      user.setEmail("1111@naver.com");
      user.setPhoneNumber("01011113333");
      user.setPassword("1234");
      user.setRole(RoleUser.NORMAL);

      Point point = new Point();
      point.setPointAmount(500);
      user.setPoint(point);
      userRepository.save(user);

      PointDTO pointDTO = new PointDTO();
      pointDTO.setPhoneNumber("01011113333");
      pointDTO.setPointMethod("phoneNumber");
      pointDTO.setStoreId(2L);
      pointDTO.setPosId(2L);
      cartRedisRepository.savePoint(pointDTO);

      String phoneNumber = cartRedisRepository.findPhoneNumber(pointDTO.getStoreId() + "-" + pointDTO.getPosId());

      assertThat(phoneNumber).isEqualTo(pointDTO.getPhoneNumber());


    }

  @Test
  void findUserId() throws Exception {
    User user = new User();
    user.setName("홍길동1");
    user.setEmail("1111@naver.com");
    user.setPhoneNumber("01011113333");
    user.setPassword("1234");
    user.setRole(RoleUser.NORMAL);
    Point point = new Point();
    point.setPointAmount(500);
    user.setPoint(point);
    userRepository.save(user);

    PointDTO pointDTO = new PointDTO();
    pointDTO.setPhoneNumber("01011113333");
    pointDTO.setPointMethod("phoneNumber");
    pointDTO.setStoreId(3L);
    pointDTO.setPosId(1L);
    cartRedisRepository.savePoint(pointDTO);
    String compositeId = pointDTO.getStoreId() + "-" + pointDTO.getPosId();
    Long userId = cartRedisRepository.findUserId(compositeId);

    assertThat(userId).isEqualTo(user.getId());
  }

    @Test
    @DisplayName("카트 정보 redis 저장")
    public void readCartInfoFromRedisWithPosId () throws Exception {
      CartAddRequestDTO requestDTO = new CartAddRequestDTO();
      requestDTO.setPosId(2L);
      requestDTO.setStoreId(2L);
      requestDTO.setTotalPrice(10000);

      String compositeId = requestDTO.getStoreId() + "-" + requestDTO.getPosId();
      cartRedisRepository.delete(compositeId);
      Map<String, Map<String, List<Object>>> all = cartRedisRepository.findAll();
      System.out.println("all = " + all);
      

      List<CartAddDTO> cartItemList = new ArrayList<>();

      CartAddDTO cartAddDTO1 = new CartAddDTO();
      cartAddDTO1.setProductId(5L);
      cartAddDTO1.setCartQty(5);
      cartItemList.add(cartAddDTO1);

      CartAddDTO cartAddDTO2 = new CartAddDTO();
      cartAddDTO2.setProductId(2L);
      cartAddDTO2.setCartQty(5);
      cartItemList.add(cartAddDTO2);

      requestDTO.setCartItemList(cartItemList);

      cartRedisRepository.saveCart(requestDTO);
      Map<String, Map<String, List<Object>>> all2 = cartRedisRepository.findAll();
      System.out.println("all2 = " + all2);

      Map<String, List<Object>> cartData = cartRedisRepository.findById(compositeId);
      assertNotNull(cartData);
      assertEquals(4, cartData.size());// cartItemList, orderName, totalPrice, totalOriginPrice

      List<Object> cartList = cartData.get("cartList");
      assertNotNull(cartList);
      assertEquals(2, cartList.size());
      

    }

    @Test
    @DisplayName("포인트 redis 저장")
    public void readPointInfoFromRedisAll () throws Exception {

      PointDTO pointDTO = new PointDTO();
      pointDTO.setPhoneNumber("01012345678");
      pointDTO.setPointMethod("phoneNumber");
      pointDTO.setStoreId(2L);
      pointDTO.setPosId(2L);
      

      String compositeId = pointDTO.getStoreId() + "-" + pointDTO.getPosId();
      cartRedisRepository.delete(compositeId);

      cartRedisRepository.savePoint(pointDTO);
      Map<String, Map<String, List<Object>>> all = cartRedisRepository.findAll();
      System.out.println("all = " + all);
      Map<String, List<Object>> byId = cartRedisRepository.findById(compositeId);

      Assertions.assertEquals(3, byId.size()); // phoneNumber, pointMethod, userId


    }


  @Test
  @DisplayName("해당 posId 삭제")
  public void deleteFromRedisWithPosId() throws Exception {
    PosStoreCompositeId posStoreCompositeId = new PosStoreCompositeId();
    posStoreCompositeId.setPos_id(2L);
    posStoreCompositeId.setStore_id(2L);

    CartAddRequestDTO requestDTO = new CartAddRequestDTO();
    requestDTO.setPosId(posStoreCompositeId.getPos_id());
    requestDTO.setStoreId(posStoreCompositeId.getStore_id());
    requestDTO.setTotalPrice(10000);

    List<CartAddDTO> cartItemList = new ArrayList<>();

    CartAddDTO cartAddDTO1 = new CartAddDTO();
    cartAddDTO1.setProductId(5L);
    cartAddDTO1.setCartQty(5);
    cartItemList.add(cartAddDTO1);
    requestDTO.setCartItemList(cartItemList);

    cartRedisRepository.saveCart(requestDTO);

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

