package com.ssg.webpos.service;

import com.ssg.webpos.domain.*;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.domain.enums.PayMethod;
import com.ssg.webpos.dto.cartDto.CartAddDTO;
import com.ssg.webpos.dto.PaymentsDTO;
import com.ssg.webpos.repository.PointUseHistoryRepository;
import com.ssg.webpos.repository.cart.CartRedisRepository;
import com.ssg.webpos.repository.UserRepository;
import com.ssg.webpos.repository.cart.CartRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import com.ssg.webpos.repository.pos.PosRepository;
import com.ssg.webpos.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentsService {
  private final OrderRepository orderRepository;
  private final CartRedisRepository cartRedisRepository;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;
  private final CartRepository cartRepository;
  private final CouponService couponService;

  private final PointService pointService;

  private final PosRepository posRepository;
  private final PointUseHistoryService pointUseHistoryService;
  private final PointUseHistoryRepository pointUseHistoryRepository;
  private final PointSaveHistoryService pointSaveHistoryService;


  @Value("${api_key}")
  private String api_key;

  @Value("${api_secret}")
  private String api_secret;

  public void processPaymentCallback(PaymentsDTO paymentsDTO) {
    try {
      String error_msg = paymentsDTO.getError_msg();
      String name = paymentsDTO.getName();
      String impUid = paymentsDTO.getImp_uid();
      String merchantUid = paymentsDTO.getMerchant_uid();
      BigDecimal finalTotalPrice = paymentsDTO.getPaid_amount();
      System.out.println("finalTotalPrice = " + finalTotalPrice);
      System.out.println("name = " + name);
      System.out.println("merchantUid = " + merchantUid);
      System.out.println("impUid1 = " + impUid);

      String posId = String.valueOf(paymentsDTO.getPosId());
      String storeId = String.valueOf(paymentsDTO.getStoreId());
      String compositeId = storeId + "-" + posId;

      User user = null;
      Order order = null;

      Long userId = cartRedisRepository.findUserId(compositeId);
      if (userId != null) {
        user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found."));

      }

      Pos pos = posRepository.findById(new PosStoreCompositeId(paymentsDTO.getPosId(), paymentsDTO.getStoreId()))
          .orElseThrow(() -> new RuntimeException("Pos not found"));

      Integer totalPrice = cartRedisRepository.findTotalPrice(compositeId);
      Integer totalOriginPrice = cartRedisRepository.findTotalOriginPrice(compositeId);
      String orderName = cartRedisRepository.findOrderName(compositeId);
      // createOrder
      order = createOrder(paymentsDTO, compositeId, user, pos, finalTotalPrice, totalPrice, totalOriginPrice, orderName);
      System.out.println("orderName = " + orderName);
      System.out.println("totalOriginPrice = " + totalOriginPrice);

      // Save order
      orderRepository.save(order);

      List<Map<String, Object>> cartItemList = cartRedisRepository.findCartItems(compositeId); // 캐싱된 cartItemList 가져오기

      for (Map<String, Object> cartItem : cartItemList) {
        CartAddDTO cartAddDTO = new CartAddDTO();
        cartAddDTO.setProductId((Long) cartItem.get("productId"));
        cartAddDTO.setCartQty((int) cartItem.get("cartQty"));
        Product product = updateStockAndAddToCart(cartAddDTO);
        List<Cart> cartList = order.getCartList();
        Cart cart = new Cart(product, order);
        cart.setQty(cartAddDTO.getCartQty());
        cartList.add(cart);
        cartRepository.saveAll(cartList);
      }


        Long findUserId = cartRedisRepository.findUserId(compositeId);
        if (findUserId != null) {
            // Deduct points
            Integer pointUseAmount = cartRedisRepository.findPointAmount(compositeId);
          if (pointUseAmount != null) {
            pointService.deductPoints(userId, pointUseAmount);
            PointUseHistory pointUseHistory = new PointUseHistory(pointUseAmount, user, order);
            pointUseHistoryService.savePointUseHistory(pointUseHistory);
            user.getPointUseHistoryList().add(pointUseHistory);
          }

            // Save PointSaveHistory
          int pointSaveAmount = pointService.updatePoint(findUserId, finalTotalPrice.intValue());
          PointSaveHistory pointSaveHistory = new PointSaveHistory(pointSaveAmount, user, order);
          pointSaveHistoryService.savePointSaveHistory(pointSaveHistory);
          user.getPointSaveHistoryList().add(pointSaveHistory);
        }

//         cartRedisRepository.delete(compositeId);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Order createOrder (PaymentsDTO paymentsDTO, String compositeId, User user, Pos pos,
                             BigDecimal finalTotalPrice, Integer totalPrice, Integer totalOriginPrice, String OrderName) {
    Order order = new Order();
    order.setOrderDate(LocalDateTime.now());
    List<Order> orderList = orderRepository.findAll();
    String serialNumber = generateSerialNumber(orderList);
    order.setSerialNumber(serialNumber);
    order.setPos(pos);
    order.setUser(user);
    order.setFinalTotalPrice(finalTotalPrice.intValue());
    order.setTotalPrice(totalPrice);
    order.calcProfit(finalTotalPrice.intValue(), totalOriginPrice);
    order.setTotalOriginPrice(totalOriginPrice);
    order.setOrderName(OrderName);
    pos.getOrderList().add(order);
    System.out.println("pos.getOrderList() = " + pos.getOrderList());


      order.setOrderStatus(OrderStatus.SUCCESS);

    String pgProvider = paymentsDTO.getPg();
    if (pgProvider.equals("kakaopay")) {
      order.setPayMethod(PayMethod.KAKAO_PAY);
    } else if (pgProvider.equals("kcp")) {
      order.setPayMethod(PayMethod.CREDIT_CARD);
    } else if (pgProvider.equals("smilepay")) {
      order.setPayMethod(PayMethod.SMILE_PAY);
    } else {
      order.setPayMethod(PayMethod.Ali_pay);
    }
    // 쿠폰 deductedPrice
    Integer deductedPrice = cartRedisRepository.findDeductedPrice(compositeId);
    System.out.println("paymentdeductedPrice = " + deductedPrice);
    if (deductedPrice != null) {
      order.setCouponUsePrice(deductedPrice);;
      Long couponId = cartRedisRepository.findCouponId(compositeId);
      Coupon coupon = couponService.updateCouponStatusToUsed(couponId);
      coupon.setOrder(order);
      order.getCouponList().add(coupon);
      if (user != null) {
          coupon.setUser(user);
      }

      }

    return order;
  }

  private String generateSerialNumber(List<Order> orderList) {
    Long newOrderId = orderList.size() + 1L;
    String serialNumber = String.format("%03d", newOrderId);
    String orderDateStr = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
    String combinedStr = orderDateStr + serialNumber;
    return combinedStr;
  }

  private Product updateStockAndAddToCart(CartAddDTO cartAddDTO) {
    Product product = productRepository.findById(cartAddDTO.getProductId())
        .orElseThrow(() -> new RuntimeException("Product not found."));

    int orderQty = cartAddDTO.getCartQty();

    if (product.getStock() < orderQty) {
      throw new RuntimeException("재고가 부족합니다. 현재 재고 수: " + product.getStock() + "개");
    }
      product.minusStockQuantity(orderQty);
      productRepository.save(product);
      return product;

  }

}