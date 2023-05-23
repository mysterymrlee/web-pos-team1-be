package com.ssg.webpos.controller;

import com.ssg.webpos.domain.Delivery;
import com.ssg.webpos.domain.enums.DeliveryStatus;
import com.ssg.webpos.dto.delivery.DeliveryAddDTO;
import com.ssg.webpos.dto.delivery.DeliveryAddressDTO;
import com.ssg.webpos.repository.delivery.DeliveryRedisRepository;
import com.ssg.webpos.repository.delivery.DeliveryRepository;
import com.ssg.webpos.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/delivery")
public class DeliveryController {
  @Autowired
  DeliveryService deliveryService;
  @Autowired
  DeliveryRepository deliveryRepository;
  @Autowired
  DeliveryRedisRepository deliveryRedisRepository;

  @GetMapping("")
  public ResponseEntity getDeliveryInfo() {
    Map<String, Map<String, List<Object>>> findAll = null;
    try {
      findAll = deliveryRedisRepository.findAll();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return new ResponseEntity(findAll, HttpStatus.OK);
  }

  @PostMapping("/add")
  public ResponseEntity addDeliveryInfo(@RequestBody DeliveryAddDTO deliveryAddDTO) {
    System.out.println(deliveryAddDTO);
    try {
      deliveryRedisRepository.saveDelivery(deliveryAddDTO);
      return new ResponseEntity(HttpStatus.CREATED);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/list")
  public ResponseEntity getUserAllDeliveryList() {
    try {
      List<DeliveryAddressDTO> userAllDeliveryList = deliveryService.getUserAllDeliveryList();
      return new ResponseEntity(userAllDeliveryList, HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }
  }

  @PostMapping("/select/{id}")
  public ResponseEntity getSelectedDeliveryAddress(@PathVariable long id) {
    try {
      DeliveryAddressDTO selectedDeliveryAddress = deliveryService.getSelectedDeliveryAddress(id);
      selectedDeliveryAddress.setStoreId(1L);
      selectedDeliveryAddress.setPosId(1L);
      deliveryRedisRepository.saveSelectedDelivery(selectedDeliveryAddress);
      return new ResponseEntity(selectedDeliveryAddress, HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  }

  // 배송 상태 변경
  // 결제 완료
  @GetMapping("/complete-payment/{serialNumber}")
  public ResponseEntity setStatusCompletePayment(@PathVariable String serialNumber) {
    try {
      Delivery findDelivery = deliveryRepository.findBySerialNumber(serialNumber);
      System.out.println("findDelivery = " + findDelivery);
      findDelivery.setDeliveryStatus(DeliveryStatus.COMPLETE_PAYMENT);
      deliveryRepository.save(findDelivery);
      return new ResponseEntity(findDelivery, HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity("일치하는 배송 일련번호가 없습니다.", HttpStatus.BAD_REQUEST);
    }
  }

  // 상품 준비 중
  @GetMapping("/prepare-product/{serialNumber}")
  public ResponseEntity setStatusPrepareProduct(@PathVariable String serialNumber) {
    try {
      Delivery findDelivery = deliveryRepository.findBySerialNumber(serialNumber);
      System.out.println("findDelivery = " + findDelivery);
      findDelivery.setDeliveryStatus(DeliveryStatus.PREPARE_PRODUCT);
      deliveryRepository.save(findDelivery);
      return new ResponseEntity(findDelivery, HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity("일치하는 배송 일련번호가 없습니다.", HttpStatus.BAD_REQUEST);
    }
  }

  // 배송 준비 중
  @GetMapping("/prepare-delivery/{serialNumber}")
  public ResponseEntity setStatusPrepareDelivery(@PathVariable String serialNumber) {
    try {
      Delivery findDelivery = deliveryRepository.findBySerialNumber(serialNumber);
      System.out.println("findDelivery = " + findDelivery);
      findDelivery.setDeliveryStatus(DeliveryStatus.PREPARE_DELIVERY);
      deliveryRepository.save(findDelivery);
      return new ResponseEntity(findDelivery, HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity("일치하는 배송 일련번호가 없습니다.", HttpStatus.BAD_REQUEST);
    }
  }

  // 배송 중
  @GetMapping("/process-delivery/{serialNumber}")
  public ResponseEntity setStatusProcessDelivery(@PathVariable String serialNumber) {
    try {
      Delivery findDelivery = deliveryRepository.findBySerialNumber(serialNumber);
      System.out.println("findDelivery = " + findDelivery);
      findDelivery.setDeliveryStatus(DeliveryStatus.PROCESS_DELIVERY);
      findDelivery.setStartedDate(LocalDateTime.now());
      deliveryRepository.save(findDelivery);
      return new ResponseEntity(findDelivery, HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity("일치하는 배송 일련번호가 없습니다.", HttpStatus.BAD_REQUEST);
    }
  }

  // 배송 완료
  @GetMapping("/complete-delivery/{serialNumber}")
  public ResponseEntity setStatusCompleteDelivery(@PathVariable String serialNumber) {
    try {
      Delivery findDelivery = deliveryRepository.findBySerialNumber(serialNumber);
      System.out.println("findDelivery = " + findDelivery);
      findDelivery.setDeliveryStatus(DeliveryStatus.COMPLETE_DELIVERY);
      findDelivery.setFinishedDate(LocalDateTime.now());
      deliveryRepository.save(findDelivery);
      return new ResponseEntity(findDelivery, HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity("일치하는 배송 일련번호가 없습니다.", HttpStatus.BAD_REQUEST);
    }
  }
}