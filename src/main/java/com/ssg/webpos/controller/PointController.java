package com.ssg.webpos.controller;

import com.ssg.webpos.domain.Cart;
import com.ssg.webpos.domain.User;
import com.ssg.webpos.dto.PhoneNumberDTO;
import com.ssg.webpos.dto.PhoneNumberRequestDTO;
import com.ssg.webpos.repository.CartRedisRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/point")
public class PointController {

  @Autowired
  CartRedisRepository cartRedisRepository;

  @GetMapping("")
  public ResponseEntity<List<User>> getCartList() throws Exception {
    Map<String, Map<String, List<Object>>> all = cartRedisRepository.findAll();
    System.out.println("all = " + all);
    return new ResponseEntity(all, HttpStatus.OK);
  }

  @PostMapping("/add")
  public ResponseEntity addPoint(@RequestBody PhoneNumberRequestDTO requestDTO) {
    List<PhoneNumberDTO> phoneNumberList = requestDTO.getPhoneNumberList();
    for (PhoneNumberDTO phoneNumberDTO : phoneNumberList) {
      cartRedisRepository.savePoint(phoneNumberDTO);
    }
    return new ResponseEntity(HttpStatus.NO_CONTENT);

  }
}
