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
    paymentsService.processPaymentCallback(paymentsDTO);

    // 응답 처리 및 반환
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.add("Content-Type", "application/json; charset=UTF-8");
    JSONObject responseObj = new JSONObject();
    responseObj.put("process_result", "결제 성공");

    return new ResponseEntity<String>(responseObj.toString(), responseHeaders, HttpStatus.OK);
  }
}
