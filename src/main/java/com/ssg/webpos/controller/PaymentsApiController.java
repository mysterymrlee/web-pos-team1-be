
package com.ssg.webpos.controller;

import com.ssg.webpos.dto.PaymentsDTO;
import com.ssg.webpos.repository.cart.CartRedisRepository;
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

import java.util.Date;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
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



  @GetMapping("")
  public String welcome(Map<String, Object> model) {
    model.put("time", new Date());
    model.put("message", this.message);
    return "Welcome";
  }

  @PostMapping("/callback-receive")
  public ResponseEntity callback_receive(@RequestBody PaymentsDTO paymentsDTO) {
    paymentsDTO.getStoreId();
    paymentsDTO.getPosId();
    paymentsService.processPaymentCallback(paymentsDTO);

    // 응답 처리
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.add("Content-Type", "application/json; charset=UTF-8");
    JSONObject responseObj = new JSONObject();
    responseObj.put("process_result", "결제 성공");


    return new ResponseEntity(responseObj, responseHeaders, HttpStatus.OK);
  }
}