package com.ssg.webpos.controller;

import com.ssg.webpos.domain.PosStoreCompositeId;
import com.ssg.webpos.domain.User;
import com.ssg.webpos.dto.PointResponseDTO;
import com.ssg.webpos.dto.point.PointDTO;
import com.ssg.webpos.dto.point.PointRequestDTO;
import com.ssg.webpos.dto.point.PointUseRequestDTO;
import com.ssg.webpos.dto.point.PointUseResponseDTO;
import com.ssg.webpos.repository.UserRepository;
import com.ssg.webpos.repository.cart.CartRedisRepository;
import com.ssg.webpos.service.PointService;
import com.ssg.webpos.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/point")
@Slf4j
public class PointController {

  @Autowired
  CartRedisRepository cartRedisRepository;

  @Autowired
  UserService userService;
  @Autowired
  PointService pointService;
  @Autowired
  UserRepository userRepository;

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

    String phoneNumber = requestDTO.getPhoneNumber();
    String pointMethod = requestDTO.getPointMethod();

    // 요청한 회원이 존재하는지 여부
//    boolean isMemberExist = userService.checkMemberExist(phoneNumbers);
    Optional<User> findUser = userRepository.findByPhoneNumber(phoneNumber);

    if (findUser.isPresent()) {
      PointDTO pointDTO = new PointDTO();
      pointDTO.setPointMethod(pointMethod);
      pointDTO.setPhoneNumber(phoneNumber);
      pointDTO.setPosId(requestDTO.getPosId());
      pointDTO.setStoreId(requestDTO.getStoreId());
      cartRedisRepository.savePoint(pointDTO);

      PointResponseDTO responseDTO = userService.login(findUser.get(), new PosStoreCompositeId(requestDTO.getPosId(), requestDTO.getStoreId()));
      log.info("responseDTO: ", responseDTO);
      return new ResponseEntity(responseDTO, HttpStatus.OK);
    } else {
      // 회원 테이블에 존재하지 않는 전화번호인 경우
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
  }

  @PostMapping("/use")
  public ResponseEntity usePoint(@RequestBody @Valid PointUseRequestDTO requestDTO) throws Exception {
    Long userId = requestDTO.getUserId();
    boolean isMemberExist = userService.checkMemberExistByUserId(userId);
    if (isMemberExist) {
      int pointAmount = pointService.getPointAmount(userId);
      PointUseResponseDTO responseDTO = new PointUseResponseDTO(pointAmount);
      return new ResponseEntity(responseDTO, HttpStatus.OK);
    } else {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    }
  }
