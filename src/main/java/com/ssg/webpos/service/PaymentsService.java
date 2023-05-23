package com.ssg.webpos.service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.ssg.webpos.domain.*;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.domain.enums.PayMethod;
import com.ssg.webpos.dto.PaymentsDTO;
import com.ssg.webpos.repository.PointUseHistoryRepository;
import com.ssg.webpos.repository.cart.CartRedisRepository;
import com.ssg.webpos.repository.UserRepository;
import com.ssg.webpos.repository.cart.CartRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import com.ssg.webpos.repository.pos.PosRepository;
import com.ssg.webpos.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.ssg.webpos.domain.Order.createOrder;

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
  private final PointHistoryService pointHistoryService;
  private final PointUseHistoryRepository pointUseHistoryRepository;


  @Value("${api_key}")
  private String api_key;

  @Value("${api_secret}")
  private String api_secret;

  public void processPaymentCallback(PaymentsDTO paymentsDTO) {
    try {
      boolean success = paymentsDTO.isSuccess();
      String error_msg = paymentsDTO.getError_msg();
      System.out.println("success = " + success);

//      IamportClient ic = new IamportClient(api_key, api_secret);
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

      order = createOrder(paymentsDTO, compositeId, user, pos, finalTotalPrice, totalPrice, success);

      // Save order
      Order savedOrder = orderRepository.save(order);
      List<Map<String, Object>> cartItems = cartRedisRepository.findCartItems(compositeId);

      for (Map<String, Object> cartItem : cartItems) {
        Cart cart = new Cart();
        Long productId = (Long) cartItem.get("productId");
        int cartQty = (int) cartItem.get("cartQty");

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        cart.setQty(cartQty);
        cart.setProduct(product);
        cart.setOrder(savedOrder);

        // Save cart
        cartRepository.save(cart);
      }

      if (success) { // Payment success
        Long findUserId = cartRedisRepository.findUserId(compositeId);
        if (findUserId != null) {
            // Deduct points
            Integer pointAmount = cartRedisRepository.findPointAmount(compositeId);
            System.out.println("findUserId = " + findUserId);
            System.out.println(finalTotalPrice.intValue());

            // Update points
            pointService.updatePoint(findUserId, finalTotalPrice.intValue());
          if (pointAmount != null) {
            pointService.deductPoints(userId, pointAmount);
            PointUseHistory pointUseHistory = new PointUseHistory();
            pointUseHistory.setAmount(pointAmount);
            pointUseHistory.setUser(user);
            pointUseHistory.setOrder(order);

            // Save pointUseHistory
            pointHistoryService.savePointHistory(pointUseHistory);
          }
        }

        // cartRedisRepository.delete(compositeId);
      } else { // Payment failed
        System.out.println("error_msg = " + error_msg);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Order createOrder(PaymentsDTO paymentsDTO, String compositeId, User user, Pos pos,
                             BigDecimal finalTotalPrice, Integer totalPrice, boolean success) {
    Order order = new Order();
    order.setOrderDate(LocalDateTime.now());
    List<Order> orderList = orderRepository.findAll();
    String serialNumber = generateSerialNumber(orderList);
    order.setSerialNumber(serialNumber);
    order.setPos(pos);
    order.setUser(user);
    order.setFinalTotalPrice(finalTotalPrice.intValue());
    order.setTotalPrice(totalPrice);

    if (success) {
      order.setOrderStatus(OrderStatus.SUCCESS);
    } else {
      order.setOrderStatus(OrderStatus.FAIL);
    }

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
      order.setCouponUsePrice(deductedPrice);
      Long couponId = cartRedisRepository.findCouponId(compositeId);
      if (success) {
        couponService.updateCouponStatusToUsed(couponId);
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

}
