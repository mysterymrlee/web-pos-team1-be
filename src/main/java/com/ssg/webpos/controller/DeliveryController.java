package com.ssg.webpos.controller;

import com.ssg.webpos.domain.Delivery;
import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.enums.DeliveryStatus;
import com.ssg.webpos.dto.delivery.DeliveryCheckResponseDTO;
import com.ssg.webpos.dto.delivery.*;
import com.ssg.webpos.dto.msg.MessageDTO;
import com.ssg.webpos.repository.UserRepository;
import com.ssg.webpos.repository.cart.CartRedisImplRepository;
import com.ssg.webpos.repository.delivery.DeliveryAddressRepository;
import com.ssg.webpos.repository.delivery.DeliveryRedisRepository;
import com.ssg.webpos.repository.delivery.DeliveryRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import com.ssg.webpos.service.DeliveryService;
import com.ssg.webpos.service.SmsService;
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
  DeliveryAddressRepository deliveryAddressRepository;
  @Autowired
  DeliveryRedisRepository deliveryRedisRepository;
  @Autowired
  CartRedisImplRepository cartRedisImplRepository;
  @Autowired
  UserRepository userRepository;
  @Autowired
  OrderRepository orderRepository;
  @Autowired
  SmsService smsService;

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
  public ResponseEntity addDeliveryInfo(@RequestBody DeliveryRedisAddRequestDTO deliveryRedisAddRequestDTO) {
    deliveryService.addUserDeliveryAddress(deliveryRedisAddRequestDTO);
    return new ResponseEntity(HttpStatus.CREATED);
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

  @PostMapping("/select-delivery")
  public ResponseEntity getSelectedDeliveryAddress(@RequestBody DeliveryListRedisSelectRequestDTO deliveryListRedisSelectRequestDTO) {
    deliveryRedisRepository.saveSelectedDelivery(deliveryListRedisSelectRequestDTO);
    return new ResponseEntity(HttpStatus.CREATED);
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

      return new ResponseEntity(HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
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

      return new ResponseEntity(HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
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

      return new ResponseEntity(HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  }

  // 배송 중
  @GetMapping("/process-delivery/{serialNumber}")
  public ResponseEntity setStatusProcessDelivery(@PathVariable String serialNumber, MessageDTO messageDTO) {
    try {
      Delivery findDelivery = deliveryRepository.findBySerialNumber(serialNumber);
      String phoneNumber = findDelivery.getPhoneNumber();
      messageDTO.setTo(phoneNumber);
      Order findOrder = orderRepository.findByDeliveryId(findDelivery.getId());
      System.out.println("findOrder = " + findOrder);
      System.out.println("findDelivery = " + findDelivery);
      findDelivery.setDeliveryStatus(DeliveryStatus.PROCESS_DELIVERY);
      findDelivery.setStartedDate(LocalDateTime.now());
      deliveryRepository.save(findDelivery);
      smsService.sendSms(messageDTO, findDelivery, findOrder);
      return new ResponseEntity(HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  }

  // 배송 완료
  @GetMapping("/complete-delivery/{serialNumber}")
  public ResponseEntity setStatusCompleteDelivery(@PathVariable String serialNumber, MessageDTO messageDTO) {
    try {
      Delivery findDelivery = deliveryRepository.findBySerialNumber(serialNumber);
      String phoneNumber = findDelivery.getPhoneNumber();
      System.out.println("findDelivery = " + findDelivery);
      messageDTO.setTo(phoneNumber);
      Order findOrder = orderRepository.findByDeliveryId(findDelivery.getId());
      System.out.println("findOrder = " + findOrder);
      findDelivery.setDeliveryStatus(DeliveryStatus.COMPLETE_DELIVERY);
      findDelivery.setFinishedDate(LocalDateTime.now());
      deliveryRepository.save(findDelivery);
      smsService.sendSms(messageDTO, findDelivery, findOrder);
      return new ResponseEntity(HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/check-delivery/{serialNumber}")
  public ResponseEntity checkDelivery(@PathVariable String serialNumber) {
    try {
      Delivery findDelivery = deliveryRepository.findBySerialNumber(serialNumber);
      String msg = "";
      if (findDelivery.getDeliveryStatus().equals(DeliveryStatus.COMPLETE_PAYMENT)) {
        msg = "결제 완료되었습니다.";
      } else if (findDelivery.getDeliveryStatus().equals(DeliveryStatus.PREPARE_PRODUCT)) {
        msg = "상품 준비 중입니다.";
      } else if (findDelivery.getDeliveryStatus().equals(DeliveryStatus.PREPARE_DELIVERY)) {
        msg = "배송 준비 중입니다.";
      } else if (findDelivery.getDeliveryStatus().equals(DeliveryStatus.PROCESS_DELIVERY)) {
        msg = "배송 중입니다.";
      } else {
        msg = "배송 완료되었습니다.";
      }
      // Entity를 DTO로 변환
      DeliveryCheckResponseDTO deliveryCheckResponseDTO = new DeliveryCheckResponseDTO(findDelivery, msg);
      return new ResponseEntity(deliveryCheckResponseDTO, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  }
}