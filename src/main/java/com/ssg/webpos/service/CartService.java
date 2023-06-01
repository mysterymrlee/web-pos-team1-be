package com.ssg.webpos.service;

import com.ssg.webpos.domain.*;
import com.ssg.webpos.domain.enums.CouponStatus;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.domain.enums.PayMethod;
import com.ssg.webpos.dto.cartDto.CartAddDTO;
import com.ssg.webpos.dto.OrderDTO;
import com.ssg.webpos.repository.CouponRepository;
import com.ssg.webpos.repository.PointSaveHistoryRepository;
import com.ssg.webpos.repository.PointUseHistoryRepository;
import com.ssg.webpos.repository.UserRepository;
import com.ssg.webpos.repository.cart.CartRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import com.ssg.webpos.repository.pos.PosRepository;
import com.ssg.webpos.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
  private final OrderRepository orderRepository;
  private final CartRepository cartRepository;
  private final ProductRepository productRepository;
  private final PosRepository posRepository;
  private final UserRepository userRepository;
  private final PointUseHistoryRepository pointUseHistoryRepository;
  private final PointSaveHistoryRepository pointSaveHistoryRepository;
  private final CouponRepository couponRepository;

  // 장바구니 상품 개별 삭제
  @Transactional
  public void delCart(Long cartId) {
    Cart findCart = cartRepository.findById(cartId).get();
    Order findOrder = orderRepository.findById(findCart.getOrder().getId()).get();
    Product findProduct = findCart.getProduct();
    findOrder.minusTotalPrice(findCart.getQty() * findProduct.getSalePrice());

    cartRepository.deleteById(cartId);
  }

  // 주문 생성 후 장바구니 상품들 주문에 추가 - DB에 저장
  @Transactional
  public Order addOrder(List<CartAddDTO> cartAddDTOList, CartAddDTO cartAddDTO, OrderDTO orderDTO) {
    Pos pos = posRepository.findById(cartAddDTO.getPosStoreCompositeId()).get();
    /* cartAddDTOList.get(0).getPosId(); */

    // 주문 생성
    Order order = new Order();
    // serialNumber 생성
    List<Order> orderList = orderRepository.findAll();
    Long newOrderId = orderList.size() + 1L;

    order.setOrderDate(orderDTO.getOrderDate());
    String serialNumber = String.format("%03d", newOrderId);
    System.out.println("serialNumber = " + serialNumber); // 세 자릿수로 만들기
    String orderDateStr = orderDTO.getOrderDate().format(DateTimeFormatter.BASIC_ISO_DATE); // 날짜 형식 맞추기 ex) 20230509

    Long storeId = orderDTO.getStoreId();
    String strStoreId = String.format("%02d", storeId);
    System.out.println("strStoreId = " + strStoreId);

    Long posId = orderDTO.getPosId();
    String strPosId = String.format("%02d", posId);
    System.out.println("strPosId = " + strPosId);

    String orderSerialNumber = orderDateStr + strStoreId + strPosId + serialNumber;
    System.out.println("combinedStr = " + orderSerialNumber);
    order.setSerialNumber(orderSerialNumber);

    System.out.println("order = " + order);
    order.setOrderStatus(OrderStatus.SUCCESS);
    order.setPayMethod(PayMethod.CREDIT_CARD);
    order.setPos(pos);
    List<Cart> cartList = order.getCartList();
    System.out.println("cartList = " + cartList);

    for (CartAddDTO cDTO : cartAddDTOList) {
      Product product = productRepository.findById(cDTO.getProductId()).get();
      if (product.getStock() < cDTO.getCartQty()) {
        throw new RuntimeException("재고가 부족합니다. 현재 재고 수 : " + product.getStock() + "개");
      }
      int orderQty = cDTO.getCartQty();
      // 장바구니에 담겨있는 상품이 있는지
      Cart existingCart = cartList.stream()
          .filter(cart -> cart.getProduct().equals(product))
          .findFirst()
          .orElse(null);
      // 장바구니에 담겨있는 상품이 있으면 수량만 증가
      if (existingCart != null) {
        int currentQty = existingCart.getQty();
        int newQty = currentQty + orderQty;
        if (newQty > product.getStock()) {
          throw new RuntimeException("재고가 부족합니다. 현재 재고 수 : " + product.getStock() + "개");
        }
        existingCart.setQty(newQty);
      } else {
        if (orderQty > product.getStock()) {
          throw new RuntimeException("재고가 부족합니다. 현재 재고 수 : " + product.getStock() + "개");
        }
        Cart cart = new Cart(product, order);
        cart.setQty(orderQty);
        cartList.add(cart);
      }
      product.minusStockQuantity(orderQty);
      cartRepository.saveAll(cartList);
    }
    orderRepository.save(order);
    return order;
  }

  @Transactional
  public void cancelOrder(Long orderId, Long userId) {
    Order order = orderRepository.findById(orderId).orElseThrow(
        () -> new RuntimeException("주문 내역을 찾을 수 없습니다."));
    order.setOrderStatus(OrderStatus.CANCEL);
    User findUser = userRepository.findById(userId).get();

    // 사용한 포인트 반환
    int currentPoint = findUser.getPoint().getPointAmount();
    System.out.println("currentPoint = " + currentPoint);

    PointUseHistory findUsePoint = pointUseHistoryRepository.findByOrderId(orderId).orElseThrow(
        () -> new RuntimeException("주문 시 사용한 포인트 내역이 없습니다."));
    int usePointAmount = findUsePoint.getPointUseAmount();
    System.out.println("usePointAmount = " + usePointAmount);

    currentPoint += usePointAmount;
    System.out.println("currentPoint = " + currentPoint);

//    findUser.setPoint();
    userRepository.save(findUser);
    findUsePoint.setPointStatus((byte) 1);
    pointUseHistoryRepository.save(findUsePoint);

    System.out.println("findUsePoint = " + findUsePoint);
    System.out.println("findUser = " + findUser);

    // 적립 포인트 취소
    PointSaveHistory findSavePoint = pointSaveHistoryRepository.findByOrderId(orderId).orElseThrow(
        () -> new RuntimeException("주문 시 적립한 포인트 내역이 없습니다."));
    int savePointAmount = findSavePoint.getPointSaveAmount();
    System.out.println("savePointAmount = " + savePointAmount);

    currentPoint -= savePointAmount;
    System.out.println("currentPoint = " + currentPoint);

//    findUser.setPoint(currentPoint);
    userRepository.save(findUser);
    findSavePoint.setPointStatus((byte) 1);
    pointSaveHistoryRepository.save(findSavePoint);

    System.out.println("findSavePoint = " + findSavePoint);
    System.out.println("findUser = " + findUser);

    // 쿠폰 상태 변경
    Coupon useCoupon = couponRepository.findByOrderId(orderId);
    System.out.println("useCoupon = " + useCoupon);

    if (useCoupon != null) {
      useCoupon.setCouponStatus(CouponStatus.NOT_USED);
      couponRepository.save(useCoupon);
    } else {
      System.out.println("주문 시 사용한 쿠폰이 없습니다.");
    }

    // 재고 수량 증가
    List<Cart> cartList = order.getCartList();
    System.out.println("cartList = " + cartList);
    for (Cart cart : cartList) {
      Product product = cart.getProduct();
      System.out.println("product = " + product);
      int qty = cart.getQty();
      System.out.println("qty = " + qty);
      product.plusStockQuantity(qty);
      int stock = product.getStock();
      System.out.println("stock = " + stock);
    }

    orderRepository.save(order);
  }
}