package com.ssg.webpos.controller;

import com.ssg.webpos.dto.point.PointDTO;
import com.ssg.webpos.dto.point.PointRequestDTO;
import com.ssg.webpos.dto.point.PointUseDTO;
import com.ssg.webpos.repository.cart.CartRedisRepository;
import com.ssg.webpos.service.PointService;
import com.ssg.webpos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/point")
public class PointController {

  @Autowired
  CartRedisRepository cartRedisRepository;

  @Autowired
  UserService userService;
  @Autowired
  PointService pointService;
//  @Autowired

  @GetMapping("")
  public ResponseEntity getPointList() throws Exception {
    Map<String, Map<String, List<Object>>> all = cartRedisRepository.findAll();
    System.out.println("PointController / getPointList() / all = " + all);
    return new ResponseEntity(all, HttpStatus.OK);
  }
//  // 확인
//  @GetMapping("/user/{userId}")
//  public ResponseEntity<Integer> getUserPoint(@PathVariable("userId") Long userId) {
//    int point = pointService.getUserPoint(userId);
//    return ResponseEntity.ok(point);
//  }


  @PostMapping("/add")
  public ResponseEntity addPoint(@RequestBody @Valid PointRequestDTO requestDTO, BindingResult bindingResult) throws Exception {
    if (bindingResult.hasErrors()) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    String phoneNumbers = requestDTO.getPhoneNumber();
    String pointMethod = requestDTO.getPointMethod();

    // 요청한 회원이 존재하는지 여부
    boolean isMemberExist = userService.checkMemberExist(phoneNumbers);

    if (isMemberExist) {
      PointDTO pointDTO = new PointDTO();
      pointDTO.setPointMethod(pointMethod);
      pointDTO.setPhoneNumber(phoneNumbers);
      pointDTO.setPosId(requestDTO.getPosId());
      pointDTO.setStoreId(requestDTO.getStoreId());
      cartRedisRepository.savePoint(pointDTO);

      return new ResponseEntity(HttpStatus.OK);
    } else {
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
  }

  @PostMapping("/use")
  public ResponseEntity usePoint(@RequestBody @Valid PointUseDTO requestDTO, BindingResult bindingResult) throws Exception {
    if (bindingResult.hasErrors()) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
    int amount = requestDTO.getAmount();
    Long storeId = requestDTO.getStoreId();
    Long posId = requestDTO.getPosId();
    String compositeId = storeId + "-" + posId;
    Long userId = cartRedisRepository.findUserId(compositeId);
    if (userId != null) {
      cartRedisRepository.savePointAmount(requestDTO);
      return new ResponseEntity(HttpStatus.NO_CONTENT);
    } else {
      return new ResponseEntity("등록된 회원 없음",HttpStatus.NOT_FOUND);
    }
  }
}
