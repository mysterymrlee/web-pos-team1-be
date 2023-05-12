package com.ssg.webpos.service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.User;
import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.domain.enums.PayMethod;
import com.ssg.webpos.dto.PaymentsDTO;
import com.ssg.webpos.repository.CartRedisRepository;
import com.ssg.webpos.repository.UserRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentsService {
    private final OrderRepository orderRepository;
    private final CartRedisRepository cartRedisRepository;
    private final UserRepository userRepository;
    private final PointService pointService;

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
          int final_price = (response.getResponse().getAmount()).intValue();
          System.out.println("final_price = " + final_price);
          String name = paymentsDTO.getName();
          String impUid1 = paymentsDTO.getImp_uid();
          String merchantUid = paymentsDTO.getMerchant_uid();
          System.out.println("name = " + name);
          System.out.println("merchantUid = " + merchantUid);
          System.out.println("impUid1 = " + impUid1);

          Order order = new Order();
          order.setFinalTotalPrice(final_price);
          order.setOrderStatus(OrderStatus.SUCCESS);
          if (response.getResponse().getPgProvider().equals("kakaopay")) {
            order.setPayMethod(PayMethod.KAKAO_PAY);
          } else if (response.getResponse().getPgProvider().equals("kcp")) {
            order.setPayMethod(PayMethod.CREDIT_CARD);
          } else if (response.getResponse().getPgProvider().equals("smilepay")) {
            order.setPayMethod(PayMethod.SMILE_PAY);
          } else {
            order.setPayMethod(PayMethod.Ali_pay);
          }

          // Order를 저장
          Order savedOrder = orderRepository.save(order);
          System.out.println("savedOrder = " + savedOrder);

          // redis phoneNumber
          List<String> phoneNumbers = cartRedisRepository.findAllPhoneNumbers();
          String phoneNumber = phoneNumbers.get(0);
          System.out.println("phoneNumber = " + phoneNumber);

          // 포인트 적립
          pointService.updatePoint(phoneNumber, final_price);

        } else { // 결제 실패
          System.out.println("error_msg = " + error_msg);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

