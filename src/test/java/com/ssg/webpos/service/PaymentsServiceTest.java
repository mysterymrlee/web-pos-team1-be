package com.ssg.webpos.service;

import com.ssg.webpos.domain.*;
import com.ssg.webpos.domain.enums.CouponStatus;
import com.ssg.webpos.domain.enums.RoleUser;
import com.ssg.webpos.dto.*;
import com.ssg.webpos.dto.cartDto.CartAddDTO;
import com.ssg.webpos.dto.cartDto.CartAddRequestDTO;
import com.ssg.webpos.dto.coupon.CouponAddRequestDTO;
import com.ssg.webpos.dto.point.PointDTO;
import com.ssg.webpos.dto.point.PointUseDTO;
import com.ssg.webpos.repository.CouponRepository;
import com.ssg.webpos.repository.PointUseHistoryRepository;
import com.ssg.webpos.repository.UserRepository;
import com.ssg.webpos.repository.cart.CartRedisRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import com.ssg.webpos.repository.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
  @Autowired
  ProductRepository productRepository;
  @Autowired
  UserRepository userRepository;
  @Autowired
  PointService pointService;
  @BeforeEach
  void setup() {


  }

  @Test
  @DisplayName("결제 실패 시 재고 유지")
  void MaintenanceStockIfPaymentSuccess() throws Exception {
    Long productId1 = 1L;
    Long productId2 = 2L;
    int cartQty1 = 3;
    int cartQty2 = 5;
    saveRedisCart(productId1, productId2, cartQty1, cartQty2);
    Product product1 = productRepository.findById(productId1).get();
    Product product2 = productRepository.findById(productId2).get();
    int beforeStock1 = product1.getStock();
    int beforeStock2 = product2.getStock();

    System.out.println("beforeStock1 = " + beforeStock1);
    System.out.println("beforeStock2 = " + beforeStock2);

    PaymentsDTO paymentsDTO = new PaymentsDTO();
    paymentsDTO.setPosId(2L);
    paymentsDTO.setStoreId(2L);
    paymentsDTO.setSuccess(false);
    paymentsDTO.setName("사과");
    paymentsDTO.setPaid_amount(BigDecimal.valueOf(10000));
    paymentsDTO.setPg("kakaopay");

    paymentsService.processPaymentCallback(paymentsDTO);

    Product afterProduct1 = productRepository.findById(productId1).get();
    Product afterProduct2 = productRepository.findById(productId2).get();

    int afterStock1 = afterProduct1.getStock();
    int afterStock2 = afterProduct2.getStock();

    System.out.println("afterStock1 = " + afterStock1);
    System.out.println("afterStock2 = " + afterStock2);

    assertEquals(beforeStock1, afterStock1);
    assertEquals(beforeStock2, afterStock2);
  }

  @Test
  @DisplayName("결제 성공 시 재고 업데이트")
  void updateStockIfPaymentSuccess() throws Exception {
    Long productId1 = 1L;
    Long productId2 = 2L;
    int cartQty1 = 3;
    int cartQty2 = 5;
    saveRedisCart(productId1, productId2, cartQty1, cartQty2);
    Product product1 = productRepository.findById(productId1).get();
    Product product2 = productRepository.findById(productId2).get();
    int beforeStock1 = product1.getStock();
    int beforeStock2 = product2.getStock();

    System.out.println("beforeStock1 = " + beforeStock1);
    System.out.println("beforeStock2 = " + beforeStock2);

    PaymentsDTO paymentsDTO = new PaymentsDTO();
    paymentsDTO.setPosId(2L);
    paymentsDTO.setStoreId(2L);
    paymentsDTO.setSuccess(true);
    paymentsDTO.setName("사과");
    paymentsDTO.setPaid_amount(BigDecimal.valueOf(10000));
    paymentsDTO.setPg("kakaopay");

    paymentsService.processPaymentCallback(paymentsDTO);

    Product afterProduct1 = productRepository.findById(productId1).get();
    Product afterProduct2 = productRepository.findById(productId2).get();

    int afterStock1 = afterProduct1.getStock();
    int afterStock2 = afterProduct2.getStock();

    System.out.println("afterStock1 = " + afterStock1);
    System.out.println("afterStock2 = " + afterStock2);

    assertEquals(beforeStock1 - cartQty1, afterStock1);
    assertEquals(beforeStock2 - cartQty2, afterStock2);
  }
  @Test
  @DisplayName("장바구니 추가 후 쿠폰 적용: 쿠폰 상태 NOT_USED -> USED")
  void addToCartWithCouponIfPaymentSuccess() throws Exception {
    Long productId1 = 1L;
    Long productId2 = 2L;
    int cartQty1 = 3;
    int cartQty2 = 5;
    saveRedisCart(productId1, productId2, cartQty1, cartQty2);
    Coupon createCoupon = createCoupon();
    System.out.println("beforeCouponStatus" + createCoupon.getCouponStatus());
    saveRedisCoupon(createCoupon);
    paySuccessProcess();

    CouponStatus afterCouponStatus = createCoupon.getCouponStatus();
    System.out.println("afterCouponStatus = " + afterCouponStatus);

    assertEquals(CouponStatus.USED, afterCouponStatus);
  }

  @Test
  @DisplayName("장바구니 추가 후 쿠폰 적용: 쿠폰 상태 NOT_USED -> USED")
  void addToCartWithCouponIfPaymentFail() throws Exception {
    Long productId1 = 1L;
    Long productId2 = 2L;
    int cartQty1 = 3;
    int cartQty2 = 5;
    saveRedisCart(productId1, productId2, cartQty1, cartQty2);
    Coupon createCoupon = createCoupon();
    System.out.println("beforeCouponStatus" + createCoupon.getCouponStatus());
    saveRedisCoupon(createCoupon);
    paySuccessProcess();
    CouponStatus afterCouponStatus = createCoupon.getCouponStatus();
    System.out.println("afterCouponStatus = " + afterCouponStatus);

    assertEquals(CouponStatus.USED, afterCouponStatus);
  }

  @Test
  @DisplayName("장바구니 추가 후 쿠폰 적용 후 포인트 적립")
  void addToCartWithCouponAndPointUse() throws Exception {
    Long productId1 = 1L;
    Long productId2 = 2L;
    int cartQty1 = 3;
    int cartQty2 = 5;
    String phoneNumber = "010555555555";
    User user = new User();
    user.setName("홍길동1");
    user.setEmail("1111@naver.com");
    user.setPhoneNumber(phoneNumber);
    user.setPassword("1234");
    user.setRole(RoleUser.NORMAL);
    user.setPoint(100);
    userRepository.save(user);
    int beforePoint = userRepository.findByPhoneNumber(phoneNumber).get().getPoint();
    System.out.println("beforePoint = " + beforePoint);

    saveRedisCart(productId1, productId2, cartQty1, cartQty2);
    saveRedisPoint();
    Coupon createCoupon = createCoupon();
    saveRedisCoupon(createCoupon);

    paySuccessProcess();

    int afterPoint = userRepository.findByPhoneNumber(phoneNumber).get().getPoint();
    System.out.println("afterPoint = " + afterPoint);

    // 포인트 적립 여부 확인
    int pointEarned = afterPoint - beforePoint;
    assertEquals(10, pointEarned);

  }

  @Test
  @DisplayName("장바구니 추가 후 포인트 사용 후 포인트 적립")
  void addToCartWithPointUseAndPointSave() throws Exception {
    Long productId1 = 1L;
    Long productId2 = 2L;
    int cartQty1 = 3;
    int cartQty2 = 5;
    String phoneNumber = "010555555555";

    // 사용자 생성 및 포인트 초기화
    User user = new User();
    user.setName("홍길동1");
    user.setEmail("1111@naver.com");
    user.setPhoneNumber(phoneNumber);
    user.setPassword("1234");
    user.setRole(RoleUser.NORMAL);
    int currentPoint = 500;
    user.setPoint(currentPoint);
    userRepository.save(user);

    // 장바구니에 상품 추가
    saveRedisCart(productId1, productId2, cartQty1, cartQty2);

    // 포인트 사용 요청
    int useAmount = saveRedisPointUse(); // 20

    // 포인트 사용 후 포인트 적립

    System.out.println("user.getId() = " + user.getId());

    // 사용자 포인트 확인
    int afterPoint = userRepository.findByPhoneNumber(phoneNumber).get().getPoint();
    System.out.println("afterPoint = " + afterPoint);

    // 포인트 사용 및 적립에 대한 검증
    int expectedPoint = currentPoint - useAmount + 10; // 초기 포인트 - 사용 포인트 + 적립 포인트 = 500 - 20 + 10
    System.out.println("expectedPoint = " + expectedPoint);
    assertEquals(expectedPoint, afterPoint);
  }

  private void saveRedisCart(Long productId1, Long productId2, int cartQty1, int cartQty2) {
  PosStoreCompositeId posStoreCompositeId = new PosStoreCompositeId();
  posStoreCompositeId.setPos_id(2L);
  posStoreCompositeId.setStore_id(2L);
  CartAddRequestDTO requestDTO = new CartAddRequestDTO();
  requestDTO.setPosId(posStoreCompositeId.getPos_id());
  requestDTO.setStoreId(posStoreCompositeId.getStore_id());
  requestDTO.setTotalPrice(10000);

  List<CartAddDTO> cartItemList = new ArrayList<>();
  CartAddDTO cartAddDTO1 = new CartAddDTO();
  cartAddDTO1.setProductId(productId1);
  cartAddDTO1.setCartQty(cartQty1);
  cartItemList.add(cartAddDTO1);

  CartAddDTO cartAddDTO2 = new CartAddDTO();
  cartAddDTO2.setProductId(productId2);
  cartAddDTO2.setCartQty(cartQty2);
  cartItemList.add(cartAddDTO2);

  requestDTO.setCartItemList(cartItemList);
  cartRedisRepository.saveCart(requestDTO);

}
  private void saveRedisCoupon(Coupon coupon) throws Exception {
    CouponAddRequestDTO couponAddRequestDTO = new CouponAddRequestDTO();
    couponAddRequestDTO.setPosId(2L);
    couponAddRequestDTO.setStoreId(2L);
    couponAddRequestDTO.setSerialNumber(coupon.getSerialNumber());
    cartRedisRepository.saveCoupon(couponAddRequestDTO);

    Map<String, Map<String, List<Object>>> cartall = cartRedisRepository.findAll();
    System.out.println("cartall = " + cartall);
  }
  private Coupon createCoupon() {
    Coupon coupon = new Coupon();
    coupon.setCouponStatus(CouponStatus.NOT_USED);
    coupon.setName("500원");
    coupon.setSerialNumber("ValidTestSerialNumber3");
    coupon.setDeductedPrice(500);
    coupon.setExpiredDate(LocalDate.now().plusDays(7));
    Coupon saveCoupon = couponRepository.save(coupon);
    return saveCoupon;
  }
  private int paySuccessProcess() {
    PaymentsDTO paymentsDTO = new PaymentsDTO();
    paymentsDTO.setPosId(2L);
    paymentsDTO.setStoreId(2L);
    paymentsDTO.setSuccess(true);
    paymentsDTO.setName("사과");
    int finalTotalPrice = 10000;
    paymentsDTO.setPaid_amount(BigDecimal.valueOf(finalTotalPrice));
    paymentsDTO.setPg("kakaopay");
    paymentsService.processPaymentCallback(paymentsDTO);
    return finalTotalPrice;
  }
  private int payFailProcess() {
    PaymentsDTO paymentsDTO = new PaymentsDTO();
    paymentsDTO.setPosId(2L);
    paymentsDTO.setStoreId(2L);
    paymentsDTO.setSuccess(true);
    paymentsDTO.setName("사과");
    int finalTotalPrice = 10000;
    paymentsDTO.setPaid_amount(BigDecimal.valueOf(finalTotalPrice));
    paymentsDTO.setPg("kakaopay");
    paymentsService.processPaymentCallback(paymentsDTO);
    return finalTotalPrice;
  }

  private  void saveRedisPoint() {
    PointDTO pointDTO = new PointDTO();
    pointDTO.setPhoneNumber("010555555555");
    pointDTO.setPointMethod("phoneNumber");
    pointDTO.setStoreId(2L);
    pointDTO.setPosId(2L);
    cartRedisRepository.savePoint(pointDTO);
  }

  private int saveRedisPointUse() {
    PointUseDTO pointUseDTO = new PointUseDTO();
    pointUseDTO.setStoreId(2L);
    pointUseDTO.setPosId(2L);
    pointUseDTO.setAmount(20);
    cartRedisRepository.savePointAmount(pointUseDTO);
    return pointUseDTO.getAmount();
  }
}
