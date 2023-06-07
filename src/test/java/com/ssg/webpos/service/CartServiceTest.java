package com.ssg.webpos.service;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    Long orderId = 88L;
    Long cartId = 187L;
    Order beforeOrder = orderRepository.findById(orderId).get();
    int beforeTotalPrice = beforeOrder.getTotalPrice();
    Cart findCart = cartRepository.findById(cartId).get();
    Product findProduct = findCart.getProduct();
    int diffPrice = findCart.getQty() * findProduct.getSalePrice();

    cartService.delCart(cartId);
    Order afterOrder = orderRepository.findById(orderId).get();
    assertEquals(beforeTotalPrice - diffPrice, afterOrder.getTotalPrice());
  }


  @Test
  @DisplayName("[주문 취소] 재고량 증가 테스트")
  void cancelOrderTest() {
    Long orderId = addOrder();
    Long userId = 1L;
    Order findOrder = orderRepository.findById(orderId).get();
    System.out.println("findOrder = " + findOrder);
    // 재고 수량 저장을 위한 리스트
    List<Product> productList = new ArrayList<>();
    List<Product> allProductList = productRepository.findAll();
    List<Cart> cartList = findOrder.getCartList();

    for (Cart cart : cartList) {
      Product product = cart.getProduct();
      System.out.println("product = " + product);
      productList.add(product);
    }
    System.out.println("productList = " + productList);

    Order cancelOrder = orderRepository.findById(orderId).get();

    // 주문 취소로 인한 재고 수량 증가 확인
    for (Product cartProduct : productList) {
      int index = allProductList.indexOf(cartProduct);
      System.out.println("index = " + index);
      Product findProduct = allProductList.get(index);
      int beforeStock = findProduct.getStock();
      System.out.println("beforeStock = " + beforeStock);
      cartService.cancelOrder(orderId, userId);
      int canceledQty = (int) findOrder.getCartList().stream()
          .filter(cart -> cart.getProduct().equals(cartProduct))
          .mapToLong(Cart::getQty)
          .sum();
      System.out.println("canceledQty = " + canceledQty);

      int expectedStock = beforeStock + canceledQty;
      int actualStock = productRepository.findById(cartProduct.getId()).get().getStock();
      System.out.println("expectedStock = " + expectedStock);
      System.out.println("actualStock = " + actualStock);

      assertEquals(OrderStatus.CANCEL, cancelOrder.getOrderStatus());
      assertEquals(expectedStock, actualStock);
    }
  }

  @Test
  @DisplayName("[주문 취소] 포인트 사용 및 적립 취소, 쿠폰 반환, 재고량 증가 테스트")
  void cancelOrder() {
    Long productId1 = 11L;
    Long userId = 1L;
    User findUser = userRepository.findById(userId).get();
    System.out.println("findUser = " + findUser);

    Order order = Order.builder()
        .orderStatus(OrderStatus.SUCCESS)
        .payMethod(PayMethod.CREDIT_CARD)
        .orderName("키위 외 2건")
        .user(findUser)
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

    cartService.cancelOrder(order.getId(), userId);
    int actualResult = findPoint.getPointAmount();
    System.out.println("actualResult = " + actualResult);

    assertEquals(expectedResult, actualResult);
  }

  Long addOrder() {
    PosStoreCompositeId posStoreCompositeId = new PosStoreCompositeId();
    posStoreCompositeId.setPos_id(1L);
    posStoreCompositeId.setStore_id(1L);

    Long productId1 = 11L;
    Long productId2 = 12L;
    Long productId3 = 13L;
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
    CartAddDTO cartAddDTO3 = new CartAddDTO();
    cartAddDTO3.setPosStoreCompositeId(posStoreCompositeId);
    cartAddDTO3.setProductId(productId3);
    cartAddDTO3.setCartQty(4);

    cartAddDTOList.add(cartAddDTO1);
    cartAddDTOList.add(cartAddDTO2);
    cartAddDTOList.add(cartAddDTO3);
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
    Product product3 = productRepository.findById(productId3).get();

    int beforeStock1 = product1.getStock();
    int beforeStock2 = product2.getStock();
    int beforeStock3 = product3.getStock();

    System.out.println("beforeStock1 = " + beforeStock1);
    System.out.println("beforeStock2 = " + beforeStock2);
    System.out.println("beforeStock3 = " + beforeStock3);

    Order savedOrder = cartService.addOrder(cartAddDTOList, cartAddDTO1, orderDTO);
    System.out.println("savedOrder = " + savedOrder);

    int afterStock1 = product1.getStock();
    int afterStock2 = product2.getStock();
    int afterStock3 = product3.getStock();
    System.out.println("afterStock1 = " + afterStock1);
    System.out.println("afterStock3 = " + afterStock3);

    // then
    List<Cart> savedCarts = savedOrder.getCartList();
    for (Cart cart : savedCarts) {
      System.out.println("cart = " + cart);
    }
    System.out.println("savedCarts = " + savedCarts);
    assertEquals(3, savedCarts.size());

    // 기존에 존재하지 않는 상품의 경우
    Cart savedCart2 = savedCarts.get(2);
    System.out.println("savedCart2 = " + savedCart2);
    assertEquals(productId3, savedCart2.getProduct().getId());
    assertEquals(4, savedCart2.getQty());
    int expectedStock2 = beforeStock3 - savedCart2.getQty();
    System.out.println("expectedStock2 = " + expectedStock2);
    int actualStock2 = productRepository.findById(productId3).get().getStock();
    assertEquals(expectedStock2, actualStock2);
    return savedOrder.getId();
  }

}
