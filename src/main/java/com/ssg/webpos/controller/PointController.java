package com.ssg.webpos.controller;

import com.ssg.webpos.domain.PosStoreCompositeId;
import com.ssg.webpos.domain.User;
import com.ssg.webpos.dto.PointDTO;
import com.ssg.webpos.dto.PointRequestDTO;
import com.ssg.webpos.repository.CartRedisRepository;
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

  @GetMapping("")
  public ResponseEntity<List<User>> getCartList() throws Exception {
    Map<String, Map<String, List<Object>>> all = cartRedisRepository.findAll();
    System.out.println("all = " + all);
    return new ResponseEntity(all, HttpStatus.OK);
  }

  @PostMapping("/add")
  public ResponseEntity addPoint(@RequestBody @Valid PointRequestDTO requestDTO, BindingResult bindingResult) throws Exception {
    if (bindingResult.hasErrors()) {
      return new ResponseEntity<>("Invalid request", HttpStatus.BAD_REQUEST);
    }

    String phoneNumbers = requestDTO.getPhoneNumber();
    String pointMethod = requestDTO.getPointMethod();
    PointDTO pointDTO = new PointDTO();
    pointDTO.setPointMethod(pointMethod); // pointMethod 값을 설정
    pointDTO.setPhoneNumber(phoneNumbers);
    pointDTO.setPosStoreCompositeId(new PosStoreCompositeId(requestDTO.getPosId(), requestDTO.getStoreId()));
    cartRedisRepository.savePoint(pointDTO);

    return new ResponseEntity<>("포인트 적립이 완료되었습니다.", HttpStatus.OK);
  }
}
