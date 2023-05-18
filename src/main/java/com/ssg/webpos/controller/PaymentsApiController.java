package com.ssg.webpos.controller;

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
import com.ssg.webpos.service.PaymentsService;
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


@Slf4j
@Controller
@RequiredArgsConstructor
public class PaymentsApiController {
  @Autowired
  private final OrderRepository orderRepository;
  @Autowired
  private CartRedisRepository cartRedisRepository;

  @Autowired
  private PaymentsService paymentsService;

  @Autowired
  private UserRepository userRepository;
  @Value("${application.message:Hello World}")
  private String message = "Hello Payments World!!";



  @RequestMapping(value = "/payment/{orderId}/result", method = RequestMethod.GET)
  public ResponseEntity<?> getPaymentResult(@PathVariable("orderId") Long orderId) {
    Optional<Order> optionalOrder = orderRepository.findById(orderId);

    if (optionalOrder.isPresent()) {
      Order order = optionalOrder.get();
      // order 에서 결제 정보 가져오기
      int finalTotalPrice = order.getFinalTotalPrice();
      PayMethod payMethod = order.getPayMethod();
      OrderStatus orderStatus = order.getOrderStatus();

      // 결제 정보
      JSONObject responseObj = new JSONObject();
      responseObj.put("order_id", orderId);
      responseObj.put("final_total_price", finalTotalPrice);
      responseObj.put("pay_method", payMethod.toString());
      responseObj.put("order_status", orderStatus.toString());

      return new ResponseEntity<>(responseObj.toJSONString(), HttpStatus.OK);
    } else {
      // 주문을 찾을 수 없을 때
      return new ResponseEntity<>("주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }
  }
  @GetMapping(value = "/payment/{orderId}/user/point")
  public ResponseEntity<?> getUserPointByOrderId(@PathVariable("orderId") Long orderId) {
    Optional<Order> optionalOrder = orderRepository.findById(orderId);

    if (optionalOrder.isPresent()) {
      Order order = optionalOrder.get();

      // redis phoneNumber
      List<String> phoneNumbers = cartRedisRepository.findAllPhoneNumbers();
      String phoneNumber = phoneNumbers.get(0);

      User user = userRepository.findByPhoneNumber(phoneNumber).get();

      if (user != null) {
        JSONObject responseObj = new JSONObject();
        responseObj.put("user_id", user.getId());
        responseObj.put("user_name", user.getName());
        responseObj.put("user_point", user.getPoint());

        return new ResponseEntity<>(responseObj.toJSONString(), HttpStatus.OK);
      } else {
        return new ResponseEntity<>("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
      }
    } else {
      return new ResponseEntity<>("주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping("/")
  public String welcome(Map<String, Object> model) {
    model.put("time", new Date());
    model.put("message", this.message);
    return "Welcome";
  }

  @RequestMapping(value = "/payment/callback_receive", method = RequestMethod.POST)
  public ResponseEntity<?> callback_receive(@RequestBody PaymentsDTO paymentsDTO) {
    paymentsService.processPaymentCallback(paymentsDTO);

    // 응답 처리 및 반환
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.add("Content-Type", "application/json; charset=UTF-8");
    JSONObject responseObj = new JSONObject();
    responseObj.put("process_result", "결제 성공");



    return new ResponseEntity<String>(responseObj.toString(), responseHeaders, HttpStatus.OK);
  }
}
