package com.ssg.webpos.service;

import com.ssg.webpos.domain.Cart;
import com.ssg.webpos.domain.Coupon;
import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.PosStoreCompositeId;
import com.ssg.webpos.domain.enums.CouponStatus;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.dto.*;
import com.ssg.webpos.repository.CouponRepository;
import com.ssg.webpos.repository.PointUseHistoryRepository;
import com.ssg.webpos.repository.cart.CartRedisRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Rollback(value = false)
public class PaymentsServiceTest {
  @Autowired
  PaymentsService paymentsService;
  @Autowired
  PointUseHistoryRepository pointUseHistoryRepository;
  @Autowired
  CartRedisRepository cartRedisRepository;
  @Autowired
  CouponRepository couponRepository;
  @Autowired
  CouponService couponService;
  @Autowired
  OrderRepository orderRepository;
  @BeforeEach
  void setup() {
    PosStoreCompositeId posStoreCompositeId = new PosStoreCompositeId();
    posStoreCompositeId.setPos_id(1L);
    posStoreCompositeId.setStore_id(1L);
    CartAddRequestDTO requestDTO = new CartAddRequestDTO();
    requestDTO.setPosId(posStoreCompositeId.getPos_id());
    requestDTO.setStoreId(posStoreCompositeId.getStore_id());
    requestDTO.setTotalPrice(10000);

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
  }

  @Test
  void paymentsSuccess() throws Exception {
    PaymentsDTO paymentsDTO = new PaymentsDTO();
    paymentsDTO.setPosId(1L);
    paymentsDTO.setStoreId(1L);
    paymentsDTO.setSuccess(true);
    paymentsDTO.setName("사과");
    paymentsDTO.setPaid_amount(BigDecimal.valueOf(10000));
    paymentsDTO.setPg("kakaopay");

    paymentsService.processPaymentCallback(paymentsDTO);




  }
  @Test
  void paymentsFail() throws Exception {
    Coupon coupon = new Coupon();
    coupon.setCouponStatus(CouponStatus.NOT_USED);
    coupon.setName("500원");
    coupon.setSerialNumber("23455");
    coupon.setDeductedPrice(500);
    coupon.setExpiredDate(LocalDate.now().plusDays(7));
    couponRepository.save(coupon);

    CouponAddRequestDTO couponAddRequestDTO = new CouponAddRequestDTO();
    couponAddRequestDTO.setPosId(1L);
    couponAddRequestDTO.setStoreId(1L);
    couponAddRequestDTO.setSerialNumber(coupon.getSerialNumber());
    cartRedisRepository.saveCoupon(couponAddRequestDTO);

    Map<String, Map<String, List<Object>>> cartall = cartRedisRepository.findAll();
    System.out.println("cartall = " + cartall);



  }


  @Test
  void paymentsPointTest() throws Exception {
    PointDTO pointDTO = new PointDTO();
    pointDTO.setPhoneNumber("01012345678");
    pointDTO.setPointMethod("phoneNumber");
    pointDTO.setStoreId(1L);
    pointDTO.setPosId(1L);

//    PointUseDTO pointUseDTO = new PointUseDTO();
//    pointUseDTO.setStoreId(1L);
//    pointUseDTO.setPosId(1L);
//    pointUseDTO.setAmount(10);
//    cartRedisRepository.savePointAmount(pointUseDTO);
    cartRedisRepository.savePoint(pointDTO);


    Map<String, Map<String, List<Object>>> cartall = cartRedisRepository.findAll();
    System.out.println("cartall = " + cartall);

    PaymentsDTO paymentsDTO = new PaymentsDTO();
    paymentsDTO.setPosId(1L);
    paymentsDTO.setStoreId(1L);
    paymentsDTO.setSuccess(true);
    paymentsDTO.setName("사과");
    paymentsDTO.setPaid_amount(BigDecimal.valueOf(1000));
    paymentsDTO.setPg("kakaopay");
    paymentsService.processPaymentCallback(paymentsDTO);
  }
}
