package com.ssg.webpos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssg.webpos.domain.*;
import com.ssg.webpos.domain.enums.CouponStatus;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.domain.enums.PayMethod;
import com.ssg.webpos.dto.cartDto.CartAddDTO;
import com.ssg.webpos.dto.OrderDTO;
import com.ssg.webpos.dto.msg.MessageDTO;
import com.ssg.webpos.repository.*;
import com.ssg.webpos.repository.cart.CartRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import com.ssg.webpos.repository.pos.PosRepository;
import com.ssg.webpos.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
  private final PointRepository pointRepository;
  private final SmsService smsService;

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
  public Order cancelOrder(String merchantUid) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
    // merchantUid로 order 찾기
    Order order = orderRepository.findByMerchantUid(merchantUid);
    order.setCancelDate(LocalDateTime.now());
    System.out.println("findOrder = " + order);
    order.setOrderStatus(OrderStatus.CANCEL);
    Order save1 = orderRepository.save(order);
    System.out.println("savedOrder = " + save1);

    // 포인트 사용 내역 확인
    PointUseHistory findUsePoint = pointUseHistoryRepository.findByOrderId(order.getId()).orElse(null);
    if (findUsePoint != null) {
      int usePointAmount = findUsePoint.getPointUseAmount();
      System.out.println("usePointAmount = " + usePointAmount);
      findUsePoint.setPointStatus((byte) 1);
      System.out.println("findUsePoint = " + findUsePoint);
      pointUseHistoryRepository.save(findUsePoint);
    }

    // 적립 포인트 취소
    PointSaveHistory findSavePoint = pointSaveHistoryRepository.findByOrderId(order.getId()).orElse(null);
    if (findSavePoint != null) {
      int savePointAmount = findSavePoint.getPointSaveAmount();
      System.out.println("savePointAmount = " + savePointAmount);
      findSavePoint.setPointStatus((byte) 1);
      pointSaveHistoryRepository.save(findSavePoint);
    }

    // 쿠폰 상태 변경
    Coupon useCoupon = couponRepository.findByOrderId(order.getId());
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
    Order save = orderRepository.save(order);
    System.out.println("save = " + save);
    // 주문 취소 sms 전송
//    MessageDTO messageDTO = new MessageDTO();
//    String phoneNumber = order.getUser().getPhoneNumber();
//    System.out.println("phoneNumber = " + phoneNumber);
//    messageDTO.setTo(phoneNumber);
////    smsService.sendSms(messageDTO, null, order);
    return order;
  }
}