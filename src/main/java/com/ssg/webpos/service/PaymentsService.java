package com.ssg.webpos.service;
import com.ssg.webpos.domain.*;
import com.ssg.webpos.domain.enums.DeliveryType;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.domain.enums.PayMethod;
import com.ssg.webpos.dto.cartDto.CartAddDTO;
import com.ssg.webpos.dto.PaymentsDTO;
import com.ssg.webpos.dto.msg.MessageDTO;
import com.ssg.webpos.repository.PointRepository;
import com.ssg.webpos.repository.PointUseHistoryRepository;
import com.ssg.webpos.repository.cart.CartRedisRepository;
import com.ssg.webpos.repository.UserRepository;
import com.ssg.webpos.repository.cart.CartRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import com.ssg.webpos.repository.pos.PosRepository;
import com.ssg.webpos.repository.product.ProductRepository;
import com.ssg.webpos.service.CouponService;
import com.ssg.webpos.service.PointSaveHistoryService;
import com.ssg.webpos.service.PointService;
import com.ssg.webpos.service.PointUseHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  private final PointRepository pointRepository;
  private final DeliveryService deliveryService;
  private final SmsService smsService;


  @Value("${api_key}")
  private String api_key;

  @Value("${api_secret}")
  private String api_secret;

  @Transactional
  public void processPaymentCallback(PaymentsDTO paymentsDTO) {
    try {
      String error_msg = paymentsDTO.getError_msg();
      String name = paymentsDTO.getName();
      String impUid = paymentsDTO.getImp_uid();
      String merchantUid = paymentsDTO.getMerchant_uid();
      BigDecimal finalTotalPrice = paymentsDTO.getPaid_amount();
      int charge = paymentsDTO.getCharge();
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

      Delivery delivery = deliveryService.saveGiftInfo(paymentsDTO);
      System.out.println("delivery = " + delivery);

      // createOrder
      order = createOrder(paymentsDTO, compositeId, user, pos, finalTotalPrice, totalPrice, totalOriginPrice, orderName, charge, delivery);
      System.out.println("orderName = " + orderName);
      System.out.println("totalOriginPrice = " + totalOriginPrice);

      // Save order
//      orderRepository.save(order);
      // send sms
      MessageDTO messageDTO = new MessageDTO();
      String phoneNumber = delivery.getPhoneNumber();
      messageDTO.setTo(phoneNumber);

      DeliveryType findDeliveryType = order.getDelivery().getDeliveryType();
      System.out.println("findDeliveryType = " + findDeliveryType);
      if(order.getDelivery().getDeliveryType().equals(DeliveryType.GIFT)) {
        smsService.sendSms(messageDTO, delivery, order);
      } else if(order.getDelivery().getDeliveryType().equals(DeliveryType.DELIVERY)) {
        // 회원인 경우
        if(userId != null) {
          deliveryService.saveSelectedDelivery(paymentsDTO);
        } else {
          // 비회원인 경우
          deliveryService.saveAddedDelivery(paymentsDTO);
        }

      }

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
        Integer pointUseAmount = paymentsDTO.getPointAmount();
        // Save PointSaveHistory

        int pointSaveAmount = pointService.updatePoint(findUserId, finalTotalPrice.intValue()); // 100
        Point point = pointRepository.findByUserId(userId).get();
        PointSaveHistory pointSaveHistory = new PointSaveHistory(pointSaveAmount, order, point);
        pointSaveHistoryService.savePointSaveHistory(pointSaveHistory);
        pointSaveHistoryService.deleteExpiredPoints();


        if (pointUseAmount != null) {
          pointService.deductPoints(userId, pointUseAmount); // 포인트 사용 시 point 테이블 pointAmount 업데이트
          PointUseHistory pointUseHistory = new PointUseHistory(pointUseAmount, order, point);
          pointUseHistoryService.savePointUseHistory(pointUseHistory);
          order.setPointUsePrice(pointUseAmount);

        }



      }

//      cartRedisRepository.delete(compositeId);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Order createOrder(PaymentsDTO paymentsDTO, String compositeId, User user, Pos pos,
                            BigDecimal finalTotalPrice, Integer totalPrice, Integer totalOriginPrice, String OrderName, Integer charge, Delivery delivery) {
    Order order = new Order();
    order.setOrderDate(LocalDateTime.now());
    List<Order> orderList = orderRepository.findAll();
    String serialNumber = generateSerialNumber(orderList, paymentsDTO.getStoreId(),paymentsDTO.getPosId());
    order.setSerialNumber(serialNumber);
    order.setPos(pos);
    order.setUser(user);
    order.setDelivery(delivery);
    order.setFinalTotalPrice(finalTotalPrice.intValue());
    order.setTotalPrice(totalPrice);
    order.calcProfit(finalTotalPrice.intValue(), totalOriginPrice);
    order.setTotalOriginPrice(totalOriginPrice);
    order.setOrderName(OrderName);
    order.setCharge(charge);
    pos.getOrderList().add(order);
    System.out.println("pos.getOrderList() = " + pos.getOrderList());


    order.setOrderStatus(OrderStatus.SUCCESS);

    String pgProvider = paymentsDTO.getPg();
    if (pgProvider.equals("kakaopay")) {
      order.setPayMethod(PayMethod.KAKAO_PAY);
    } else if (pgProvider.equals("nice")) {
      order.setPayMethod(PayMethod.CREDIT_CARD);
    } else if (pgProvider.equals("kcp")) {
      order.setPayMethod(PayMethod.SAMSUNG_PAY);
    }
      // 쿠폰 deductedPrice
      Integer deductedPrice = cartRedisRepository.findDeductedPrice(compositeId);
      System.out.println("paymentdeductedPrice = " + deductedPrice);
      if (deductedPrice != null) {
        order.setCouponUsePrice(deductedPrice);
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

    // 20230530+ 01 + 01 + 0001
    private String generateSerialNumber (List < Order > orderList, Long storeId, Long posId) {
      Long newOrderId = orderList.size() + 1L;
      String serialNumber = String.format("%04d", newOrderId);
      String orderDateStr = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
      String combinedStr = orderDateStr + String.format("%02d", storeId) + String.format("%02d", posId) + serialNumber;
      return combinedStr;
    }

    private Product updateStockAndAddToCart(CartAddDTO cartAddDTO){
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
