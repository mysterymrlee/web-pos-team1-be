package com.ssg.webpos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssg.webpos.domain.*;
import com.ssg.webpos.domain.enums.CouponStatus;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.domain.enums.PayMethod;
import com.ssg.webpos.dto.cartDto.CartAddDTO;
import com.ssg.webpos.dto.OrderDTO;
import com.ssg.webpos.repository.*;
import com.ssg.webpos.repository.cart.CartRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import com.ssg.webpos.repository.pos.PosRepository;
import com.ssg.webpos.repository.product.ProductRepository;
import com.ssg.webpos.repository.store.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

@SpringBootTest
@Transactional
public class CartServiceTest {
  @Autowired
  OrderRepository orderRepository;
  @Autowired
  ProductRepository productRepository;
  @Autowired
  CartRepository cartRepository;

  @Autowired
  CartService cartService;
  @Autowired
  StoreRepository storeRepository;
  @Autowired
  PosRepository posRepository;
  @Autowired
  UserRepository userRepository;
  @Autowired
  PointUseHistoryRepository pointUseHistoryRepository;
  @Autowired
  PointSaveHistoryRepository pointSaveHistoryRepository;
  @Autowired
  CouponRepository couponRepository;
  @Autowired
  PointRepository pointRepository;

  @Test
  @DisplayName("장바구니 삭제 후 주문테이블에서 totalPrice 반영테스트")
  void delCartTest() {
    Order order = addOrder();
    System.out.println("order = " + order);
    Long orderId = order.getId();
    System.out.println("orderId = " + orderId);
    List<Cart> cartList = order.getCartList();
    System.out.println("cartList = " + cartList);

    Order beforeOrder = orderRepository.findById(orderId).get();
    System.out.println("beforeOrder = " + beforeOrder);
    int beforeTotalPrice = beforeOrder.getTotalPrice();
    System.out.println("beforeTotalPrice = " + beforeTotalPrice);
    Cart findCart = cartList.get(0);
    Product findProduct = findCart.getProduct();

    int diffPrice = findCart.getQty() * findProduct.getSalePrice();
//
    cartService.delCart(findCart.getId());
    Order afterOrder = orderRepository.findById(orderId).get();
    assertEquals(beforeTotalPrice - diffPrice, afterOrder.getTotalPrice());
  }

  @Test
  @Rollback(value = false)
  @DisplayName("[주문 취소] 포인트 사용 및 적립 취소, 쿠폰 반환, 재고량 증가 테스트")

  void cancelOrder() throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
    Long productId1 = 50L;
    Long userId = 1L;
    User findUser = userRepository.findById(userId).get();
    System.out.println("findUser = " + findUser);

    Order order = Order.builder()
        .orderStatus(OrderStatus.SUCCESS)
        .payMethod(PayMethod.CREDIT_CARD)
        .orderName("키위 외 2건")
        .user(findUser)
        .merchantUid("202306130000000103")
        .build();
    orderRepository.save(order);
    System.out.println("orderTest = " + order);

    Point point = Point.builder()
        .pointAmount(500)
        .user(findUser)
        .build();
    pointRepository.save(point);
    System.out.println("point = " + point);

    PointUseHistory pointUseHistory = new PointUseHistory();
    pointUseHistory.setPointUseAmount(10);
    pointUseHistory.setOrder(order);
    pointUseHistoryRepository.save(pointUseHistory);
    System.out.println("pointUseHistoryTest = " + pointUseHistory);

    PointSaveHistory pointSaveHistory = new PointSaveHistory();
    pointSaveHistory.setPointSaveAmount(20);
    pointSaveHistory.setOrder(order);
    pointSaveHistoryRepository.save(pointSaveHistory);
    System.out.println("pointSaveHistoryTest = " + pointSaveHistory);

    Product product1 = productRepository.findById(productId1).get();
    System.out.println("product1 = " + product1);

    List<Cart> cartList = new ArrayList<>();
    Cart cart = new Cart(product1, order);
    cart.setQty(1);
    cartRepository.save(cart);
    cartList.add(cart);
    System.out.println("cartTest = " + cart);
    System.out.println("cartListTest = " + cartList);
    order.setCartList(cartList);
    List<Cart> cartList1 = order.getCartList();
    System.out.println("cartList1 = " + cartList1);

    Coupon coupon = new Coupon();
    coupon.setCouponStatus(CouponStatus.USED);
    coupon.setName("500원");
    coupon.setSerialNumber("ValidTestSerialNumber4");
    coupon.setDeductedPrice(500);
    coupon.setExpiredDate(LocalDate.now().plusDays(7));
    System.out.println("couponTest = " + coupon);
    couponRepository.save(coupon);

    PointUseHistory findUsePoint = pointUseHistoryRepository.findByOrderId(order.getId()).get();
    System.out.println("findUsePointTest = " + findUsePoint);
    PointSaveHistory findSavePoint = pointSaveHistoryRepository.findByOrderId(order.getId()).get();
    System.out.println("findSavePointTest = " + findSavePoint);

    // 포인트 테스트
    Long pointId = findUser.getPoint().getId();
    Point findPoint = pointRepository.findById(pointId).get();
    int beforePoint = findPoint.getPointAmount();
    System.out.println("beforePointTest = " + beforePoint);
    int usePointAmount = findUsePoint.getPointUseAmount();
    System.out.println("usePointAmountTest = " + usePointAmount);
    int savePointAmount = findSavePoint.getPointSaveAmount();
    System.out.println("savePointAmountTest = " + savePointAmount);
    int expectedResult = beforePoint + usePointAmount - savePointAmount;
    System.out.println("expectedResult = " + expectedResult);

    cartService.cancelOrder(order.getMerchantUid());
    int actualResult = findPoint.getPointAmount();
    System.out.println("actualResult = " + actualResult);

    assertEquals(expectedResult, actualResult);
  }

  @Test
  public void testCancelOrder() throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
    // 테스트에 필요한 데이터 설정
    String merchantUid = "123456789";

    // 주문 생성 및 저장
    Order order = new Order();
    order.setMerchantUid(merchantUid);
    order.setOrderStatus(OrderStatus.SUCCESS);
    order.setPayMethod(PayMethod.CREDIT_CARD);
    Order savedOrder = orderRepository.save(order);

    // 적립 포인트 내역 생성 및 저장
    PointSaveHistory savePointHistory = new PointSaveHistory();
    savePointHistory.setId(99999L);
    savePointHistory.setPointStatus((byte) 0);
    // 필요한 필드 값들 설정...
    savePointHistory.setOrder(order);
    PointSaveHistory savePoint = pointSaveHistoryRepository.save(savePointHistory);

    // 포인트 사용 내역 생성 및 저장
    PointUseHistory usePointHistory = new PointUseHistory();
    // 필요한 필드 값들 설정...
    usePointHistory.setId(99999L);
    usePointHistory.setOrder(order);
    usePointHistory.setPointStatus((byte) 0);
    PointUseHistory usePoint = pointUseHistoryRepository.save(usePointHistory);

    // 쿠폰 생성 및 저장
    Coupon coupon = new Coupon();
    coupon.setCouponStatus(CouponStatus.USED);
    coupon.setOrder(order);
    coupon.setExpiredDate(LocalDate.of(2023, 12, 31));
    coupon.setName("5000원 쿠폰");
    coupon.setSerialNumber("1111111111");
    couponRepository.save(coupon);

    // 상품 재고 수량 변경
    List<Cart> cartList = savedOrder.getCartList();
    for (Cart cart : cartList) {
      Product product = cart.getProduct();
      product.plusStockQuantity(cart.getQty());
    }

    // 주문 취소 테스트
    Order canceledOrder = cartService.cancelOrder(merchantUid);

    // 테스트 결과 검증
    assertNotNull(canceledOrder);
    assertEquals(OrderStatus.CANCEL, canceledOrder.getOrderStatus());
    assertNotNull(canceledOrder.getCancelDate());

    // 포인트 사용 내역 확인
    System.out.println("savePoint = " + savePoint);
    byte savePointStatus = savePoint.getPointStatus();
    assertEquals(1, savePointStatus);

    byte usePointStatus = usePoint.getPointStatus();
    assertEquals(1, usePointStatus);


    // 쿠폰 상태 변경 확인
    Coupon updatedCoupon = couponRepository.findById(coupon.getId()).orElse(null);
    assertNotNull(updatedCoupon);
    assertEquals(CouponStatus.NOT_USED, updatedCoupon.getCouponStatus());

    // 상품 재고 수량 확인
    for (Cart cart : cartList) {
      Product product = cart.getProduct();
      assertEquals(10, product.getStock()); //
    }
  }



  Order addOrder() {
    PosStoreCompositeId posStoreCompositeId = new PosStoreCompositeId();
    posStoreCompositeId.setPos_id(1L);
    posStoreCompositeId.setStore_id(1L);

    Long productId1 = 50L;
    Long productId2 = 51L;
    List<CartAddDTO> cartAddDTOList = new ArrayList<>();
    // given
    CartAddDTO cartAddDTO1 = new CartAddDTO();
    cartAddDTO1.setPosStoreCompositeId(posStoreCompositeId);
    cartAddDTO1.setProductId(productId1);
    cartAddDTO1.setCartQty(2);
    CartAddDTO cartAddDTO2 = new CartAddDTO();
    cartAddDTO2.setPosStoreCompositeId(posStoreCompositeId);
    cartAddDTO2.setProductId(productId2);
    cartAddDTO2.setCartQty(3);

    cartAddDTOList.add(cartAddDTO1);
    cartAddDTOList.add(cartAddDTO2);
    System.out.println("cartAddDTOList = " + cartAddDTOList);
    OrderDTO orderDTO = new OrderDTO();
    orderDTO.setOrderDate(LocalDateTime.now());
    orderDTO.setPosId(1L);
    orderDTO.setStoreId(1L);

    int price = 0, qty = 0;
    for (CartAddDTO cartAddDTO : cartAddDTOList) {
      Product product = productRepository.findById(cartAddDTO.getProductId()).get();
      price += cartAddDTO.getCartQty() * product.getSalePrice();
      qty += cartAddDTO.getCartQty();
    }
    orderDTO.setTotalQuantity(qty);
    orderDTO.setFinalTotalPrice(price);

    // when
    Product product1 = productRepository.findById(productId1).get();
    Product product2 = productRepository.findById(productId2).get();

    Order savedOrder = cartService.addOrder(cartAddDTOList, cartAddDTO1, orderDTO);

    // then
    List<Cart> savedCarts = savedOrder.getCartList();
    for (Cart cart : savedCarts) {
      System.out.println("cart = " + cart);
    }
    System.out.println("savedCarts = " + savedCarts);
    assertEquals(2, savedCarts.size());

    return savedOrder;
  }

}
