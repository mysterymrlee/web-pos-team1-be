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

  @Value("${api_key}")
  String api_key;

  @Value("${api_secret}")
  String api_secret;



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

        String pgProvider = response.getResponse().getPgProvider();
        System.out.println("pgProvider = " + pgProvider);
        System.out.println("final_price = " + final_price);
        String name = paymentsDTO.getName();
        System.out.println("name = " + name);

        Order order = new Order();
        order.setTotalPrice(final_price);
        order.setId(1L);
        order.setOrderStatus(OrderStatus.SUCCESS);
        if (response.getResponse().getPgProvider().equals("kakaopay")){
          order.setPayMethod(PayMethod.KAKAO_PAY);
        }


        // Order를 저장
        Order savedOrder = orderRepository.save(order);
        System.out.println("savedOrder = " + savedOrder);

        Map<String, Map<String, List<Object>>> all = cartRedisRepository.findAll();
        System.out.println("all = " + all);
        List<String> phoneNumbers = cartRedisRepository.findAllPhoneNumbers();
        String phoneNumber = phoneNumbers.get(0);
        phoneNumber = phoneNumber.replace("[", "").replace("]", "");
        System.out.println("Phone Numbers: " + phoneNumbers);

        Optional<User> findUser = userRepository.findByPhoneNumber(phoneNumber);
        System.out.println("findUser = " + findUser);

        int point = (int) (final_price * 0.001);

        if (findUser.isPresent()) {
          User user = findUser.get();
          int currentPoint = user.getPoint();
          BigDecimal currentPointBigDecimal = new BigDecimal(currentPoint);
          BigDecimal updatedPoint = currentPointBigDecimal.add(new BigDecimal(point));

          // point 값을 업데이트
          user.setPoint(updatedPoint.intValue());
          userRepository.save(user);
          Optional<User> findUser2 = userRepository.findByPhoneNumber(phoneNumber);
          System.out.println("findUser2 = " + findUser2);
        } else {
          // 사용자를 찾을 수 없을 때
          // ...
        }
      } else { // 결제 실패
        System.out.println("error_msg = " + error_msg);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
