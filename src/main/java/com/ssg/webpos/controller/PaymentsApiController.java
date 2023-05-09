package com.ssg.webpos.controller;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.User;
import com.ssg.webpos.dto.PaymentsDTO;
import com.ssg.webpos.repository.CartRedisRepository;
import com.ssg.webpos.repository.UserRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.ssg.webpos.domain.enums.OrderStatus.SUCCESS;
import static com.ssg.webpos.domain.enums.PayMethod.KAKAO_PAY;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PaymentsApiController {
  @Autowired
  private final OrderRepository orderRepository;
  @Autowired
  private CartRedisRepository cartRedisRepository;
  @Autowired
  UserRepository userRepository;
  @Value("${application.message:Hello World}")
  private String message = "Hello Payments World!!";

  @RequestMapping("/")
  public String welcome(Map<String, Object> model) {
    model.put("time", new Date());
    model.put("message", this.message);
    return "Welcome";
  }

  @RequestMapping(value = "/payment/callback_receive", method = RequestMethod.POST)
  public ResponseEntity<?> callback_receive(@RequestBody PaymentsDTO paymentsDTO) {
    String process_result = "결제 성공!";

    // 응답 header 생성
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.add("Content-Type", "application/json; charset=UTF-8");
    JSONObject responseObj = new JSONObject();

    try {
      boolean success = paymentsDTO.isSuccess();
      String error_msg = paymentsDTO.getError_msg();
      String impUid = paymentsDTO.getImp_uid();
      String name = paymentsDTO.getName();
//      paymentsDTO.getAmount();
      System.out.println("-- callback_receive --");
      System.out.println("--------------");
      System.out.println("success = " + success);

      if (success) { // 결제 성공
        String api_key = "2826234052157240";
        String api_secret = "EGc6WP7BUyFuKVoNY4aSmxOjT7QNXCKgR8MVPnS7oxUGBBUCx2rP4JRaP13eyby4T2yLjhnBEtOw0X22";

        IamportClient ic = new IamportClient(api_key, api_secret);
        IamportResponse<Payment> response = ic.paymentByImpUid(impUid);
        String iamport_name = response.getResponse().getName(); // 상품 이름
        int total_price = (response.getResponse().getAmount()).intValue();// 결제 금액
        System.out.println("total_price = " + total_price);

        Order order = new Order();
        order.setTotalPrice(total_price);
        order.setId(1L);
        order.setOrderStatus(SUCCESS);
        order.setPayMethod(KAKAO_PAY);

        // Order를 저장
        Order savedOrder = orderRepository.save(order);
        System.out.println("savedOrder = " + savedOrder);




        String payMethod = response.getResponse().getPayMethod();
        System.out.println("iamport_name = " + iamport_name);
        System.out.println("total_price = " + total_price);
        System.out.println("payMethod = " + payMethod);

        Map<String, Map<String, List<Object>>> all = cartRedisRepository.findAll();
        System.out.println("all = " + all);
        List<String> phoneNumbers = cartRedisRepository.findAllPhoneNumbers();
        String phoneNumber = phoneNumbers.get(0); // 첫 번째 전화번호를 가져옴
        phoneNumber = phoneNumber.replace("[", "").replace("]", ""); // 대괄호 제거
        System.out.println("Phone Numbers: " + phoneNumbers);

        Optional<User> findUser = userRepository.findByPhoneNumber(phoneNumber);
        System.out.println("findUser = " + findUser);

        int point = (int) (total_price * 0.001);// 0.1% 적립
        System.out.println("point = " + point);

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



        responseObj.put("process_result", "결제 성공");
      } else { // 결제 실패
        System.out.println("error_msg = " + error_msg);
        responseObj.put("process_result", "결제 실패: " + error_msg);
      }
    } catch (Exception e) {
      e.printStackTrace();
      responseObj.put("process_result", "결제 실패: 관리자에게 문의해 주세요");
    }

    return new ResponseEntity<String>(responseObj.toString(), responseHeaders, HttpStatus.OK);
  }


}
