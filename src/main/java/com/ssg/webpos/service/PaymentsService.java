package com.ssg.webpos.service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.ssg.webpos.domain.*;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.domain.enums.PayMethod;
import com.ssg.webpos.dto.CartAddDTO;
import com.ssg.webpos.dto.PaymentsDTO;
import com.ssg.webpos.repository.CartRedisRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentsService {
  private OrderRepository orderRepository;
  private CartRedisRepository cartRedisRepository;
  private UserRepository userRepository;
  private ProductRepository productRepository;
  private CartRepository cartRepository;

  private PointService pointService;

  private PosRepository posRepository;

  @Autowired
  public PaymentsService(OrderRepository orderRepository, CartRedisRepository cartRedisRepository, UserRepository userRepository, PointService pointService, ProductRepository productRepository, CartRepository cartRepository, PosRepository posRepository) {
    this.orderRepository = orderRepository;
    this.cartRedisRepository = cartRedisRepository;
    this.userRepository = userRepository;
    this.pointService = pointService;
    this.productRepository = productRepository;
    this.cartRepository = cartRepository;
    this.posRepository = posRepository;
  }

  @Value("${api_key}")
  private String api_key;

  @Value("${api_secret}")
  private String api_secret;

  public void processPaymentCallback(PaymentsDTO paymentsDTO) {
    String process_result = "결제 성공!";
    try {
      boolean success = paymentsDTO.isSuccess();
      String error_msg = paymentsDTO.getError_msg();
      String impUid = paymentsDTO.getImp_uid();

      System.out.println("-- callback_receive --");
      System.out.println("--------------");
      System.out.println("success = " + success);

      if (success) { // 결제 성공
        IamportClient ic = new IamportClient(api_key, api_secret);
        IamportResponse<Payment> response = ic.paymentByImpUid(impUid);
        String name = paymentsDTO.getName();
        String impUid1 = paymentsDTO.getImp_uid();
        String merchantUid = paymentsDTO.getMerchant_uid();
        BigDecimal finalTotalPrice = paymentsDTO.getPaid_amount();
        System.out.println("finalTotalPrice = " + finalTotalPrice);
        System.out.println("name = " + name);
        System.out.println("merchantUid = " + merchantUid);
        System.out.println("impUid1 = " + impUid1);


        String posId = String.valueOf(paymentsDTO.getPosId());
        String storeId = String.valueOf(paymentsDTO.getStoreId());

        String compositeId = storeId + "-" + posId;
        Map<String, List<Object>> posData = cartRedisRepository.findById(compositeId);



        if (posData != null) {
          List<Object> cartList = posData.get("cartList");
          if (cartList != null && !cartList.isEmpty()) {
            List<Map<String, Object>> cartItemList = new ArrayList<>();

            for (Object obj : cartList) {
              Map<String, Object> cartItem = (Map<String, Object>) obj;
              cartItemList.add(cartItem);
            }

            Long userId = cartRedisRepository.findUserId(compositeId);

            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user 찾을 수 없습니다."));


            Pos pos = posRepository.findById(new PosStoreCompositeId(paymentsDTO.getPosId(), paymentsDTO.getStoreId()))
                .orElseThrow(() -> new RuntimeException("Pos not found"));

              Order order = new Order();
              order.setOrderStatus(OrderStatus.SUCCESS);
              order.setPos(pos);
              order.setUser(user);
              order.setFinalTotalPrice(finalTotalPrice.intValue());
              if (response != null && response.getResponse() != null) {
                String pgProvider = response.getResponse().getPgProvider();
                if (pgProvider.equals("kakaopay")) {
                  order.setPayMethod(PayMethod.KAKAO_PAY);
                } else if (pgProvider.equals("kcp")) {
                  order.setPayMethod(PayMethod.CREDIT_CARD);
                } else if (pgProvider.equals("smilepay")) {
                  order.setPayMethod(PayMethod.SMILE_PAY);
                } else {
                  order.setPayMethod(PayMethod.Ali_pay);
                }
              }

              // Order를 저장
              Order savedOrder = orderRepository.save(order);
              System.out.println("savedOrder = " + savedOrder);


            for (Map<String, Object> cartItem : cartItemList) {
              Cart cart = new Cart();
              Long productId = (Long) cartItem.get("productId");
              int cartQty = (int) cartItem.get("cartQty");

              // 상품 정보 조회
              Product product = productRepository.findById(productId)
                  .orElseThrow(() -> new RuntimeException("Product 찾을 수 없습니다."));

              cart.setQty(cartQty);
              cart.setProduct(product);
              cart.setOrder(savedOrder);

              //Cart DB에 저장
              cartRepository.save(cart);


            }
          }
        }

        // redis phoneNumber
        List<String> phoneNumbers = cartRedisRepository.findPhoneNumbersByCompositeId(compositeId);
        String phoneNumber = phoneNumbers.get(0);
        System.out.println("phoneNumber = " + phoneNumber);

        // 포인트 적립
//        pointService.updatePoint(phoneNumber, totalPrice);
        cartRedisRepository.delete(compositeId);


      } else { // 결제 실패
        System.out.println("error_msg = " + error_msg);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}