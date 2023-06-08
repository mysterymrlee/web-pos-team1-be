package com.ssg.webpos.service;

import com.ssg.webpos.domain.Delivery;
import com.ssg.webpos.domain.DeliveryAddress;
import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.User;
import com.ssg.webpos.domain.enums.DeliveryStatus;
import com.ssg.webpos.domain.enums.DeliveryType;
import com.ssg.webpos.dto.PaymentsDTO;
import com.ssg.webpos.dto.delivery.DeliveryAddDTO;
import com.ssg.webpos.dto.delivery.DeliveryAddressDTO;
import com.ssg.webpos.dto.delivery.DeliveryRedisAddRequestDTO;
import com.ssg.webpos.dto.gift.GiftRequestDTO;
import com.ssg.webpos.dto.gift.GiftSmsDTO;
import com.ssg.webpos.dto.msg.MessageDTO;
import com.ssg.webpos.repository.UserRepository;
import com.ssg.webpos.repository.cart.CartRedisImplRepository;
import com.ssg.webpos.repository.delivery.DeliveryAddressRepository;
import com.ssg.webpos.repository.delivery.DeliveryRedisImplRepository;
import com.ssg.webpos.repository.delivery.DeliveryRedisRepository;
import com.ssg.webpos.repository.delivery.DeliveryRepository;
import com.ssg.webpos.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryService {
  private final DeliveryRepository deliveryRepository;
  private final DeliveryRedisImplRepository deliveryRedisImplRepository;
  private final UserRepository userRepository;
  private final OrderRepository orderRepository;
  private final CartRedisImplRepository cartRedisImplRepository;
  private final DeliveryRedisRepository deliveryRedisRepository;
  private final DeliveryAddressRepository deliveryAddressRepository;

  // 문자열을 LocalDateTime으로 파싱
  public LocalDateTime LocalDateParse(String requestFinishedAt) throws DateTimeParseException {
    LocalDateTime dateTime = LocalDateTime.parse(requestFinishedAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    System.out.println("LocalDateTime = " + dateTime); // 2023-05-25T17:25:00
    return dateTime;
  }

  // serialNumber 생성
  public String makeSerialNumber() {
    List<Delivery> deliveryList = deliveryRepository.findAll();
    Long deliveryId = deliveryList.size() + 1L;

    LocalDateTime orderDate = LocalDateTime.now();
    String orderDateStr = orderDate.format(DateTimeFormatter.BASIC_ISO_DATE);
    System.out.println("orderDateStr = " + orderDateStr);
    String strDeliveryId = String.format("%03d", deliveryId);
    System.out.println("strDeliveryId = " + strDeliveryId);
    String deliverySerialNumber = orderDateStr + strDeliveryId;
    System.out.println("deliverySerialNumber = " + deliverySerialNumber);

    return deliverySerialNumber;
  }

  // 배송지 DB에 추가
  @Transactional
  public void addDeliveryAddress(DeliveryAddDTO deliveryDTO, Long orderId) {
    Order order = orderRepository.findById(orderId).get();
    String deliverySerialNumber = makeSerialNumber();

    Delivery delivery = Delivery.builder()
        .deliveryName(deliveryDTO.getDeliveryName())
        .userName(deliveryDTO.getUserName())
        .address(deliveryDTO.getAddress())
        .phoneNumber(deliveryDTO.getPhoneNumber())
        .requestInfo(deliveryDTO.getRequestInfo())
        .deliveryStatus(DeliveryStatus.COMPLETE_PAYMENT)
        .deliveryType(deliveryDTO.getDeliveryType())
        .requestDeliveryTime(deliveryDTO.getRequestDeliveryTime())
        .serialNumber(deliverySerialNumber)
        .postCode(deliveryDTO.getPostCode())
        .build();
    order.setDelivery(delivery);
    delivery.setOrder(order);
    deliveryRepository.save(delivery);
  }

  // 해당 유저의 저장된 모든 배송지 조회
  public List<DeliveryAddressDTO> getUserAllDeliveryList() {
    // 포인트 redis에서 userId 찾기
    List<String> findUserIdList = deliveryRedisImplRepository.findByUserId();
    long userId = Long.parseLong(findUserIdList.get(0));
    System.out.println("findUserIdList = " + findUserIdList);
    System.out.println("userId = " + userId);

    // 찾은 userId로 user 가져오기
    User user = userRepository.findById(userId).get();
    System.out.println("user = " + user);

//  deliveryList에 저장된 해당 유저의 배송지 목록을 가져와서 DTO로 변환
    List<DeliveryAddressDTO> deliveryAddressList =
        user.getDeliveryAddressList().stream()
            .map(da -> new DeliveryAddressDTO(da))
            .collect(Collectors.toList());

    System.out.println("deliveryAddressList = " + deliveryAddressList);

    return deliveryAddressList;
  }

  // redis에서 giftRecipient 정보 가져와서 DB에 저장
  public Delivery saveGiftInfo(PaymentsDTO paymentsDTO) {
    Long storeId = paymentsDTO.getStoreId();
    Long posId = paymentsDTO.getPosId();
    String compositeId = storeId + "-" + posId;
    System.out.println("compositeId = " + compositeId);
    List<Map<String, Object>> giftRecipientInfoList = deliveryRedisImplRepository.findGiftRecipientInfo(compositeId);
    String serialNumber = makeSerialNumber();
    System.out.println("serialNumber = " + serialNumber);
    Delivery savedDelivery = null;
    for (Map<String, Object> giftRecipient : giftRecipientInfoList) {
      Delivery delivery = Delivery.builder()
          .userName((String) giftRecipient.get("receiver"))
          .phoneNumber((String) giftRecipient.get("phoneNumber"))
          .sender((String) giftRecipient.get("sender"))
          .deliveryType(DeliveryType.GIFT)
          .serialNumber(serialNumber)
          .deliveryStatus(DeliveryStatus.COMPLETE_PAYMENT)
          .build();
      System.out.println("delivery = " + delivery);
      savedDelivery = deliveryRepository.save(delivery);
      String phoneNumber = savedDelivery.getPhoneNumber();
      MessageDTO messageDTO = new MessageDTO();
      messageDTO.setTo(phoneNumber);
    }
    return savedDelivery;
  }

  // redis에 캐싱된 회원 배송지 목록에서 선택된 배송지 정보 가져와서 DB에 저장
  public Delivery saveSelectedDelivery(PaymentsDTO paymentsDTO) {
    Long storeId = paymentsDTO.getStoreId();
    Long posId = paymentsDTO.getPosId();
    String compositeId = storeId + "-" + posId;
    System.out.println("compositeId = " + compositeId);
    List<Map<String, Object>> selectedDelivery = deliveryRedisImplRepository.findSelectedDelivery(compositeId);
    String serialNumber = makeSerialNumber();
    System.out.println("serialNumber = " + serialNumber);
    Delivery savedDelivery = null;
    for (Map<String, Object> selectedAddress : selectedDelivery) {
      Delivery delivery = Delivery.builder()
          .requestDeliveryTime((String) selectedAddress.get("requestDeliveryTime"))
          .address((String) selectedAddress.get("address"))
          .phoneNumber((String) selectedAddress.get("phoneNumber"))
          .deliveryName((String) selectedAddress.get("deliveryName"))
          .userName((String) selectedAddress.get("name"))
          .requestInfo((String) selectedAddress.get("requestInfo"))
          .postCode((String) selectedAddress.get("postCode"))
          .deliveryType(DeliveryType.DELIVERY)
          .serialNumber(serialNumber)
          .deliveryStatus(DeliveryStatus.COMPLETE_PAYMENT)
          .build();
      System.out.println("delivery = " + delivery);
      savedDelivery = deliveryRepository.save(delivery);
      System.out.println("savedDelivery = " + savedDelivery);
    }
    return savedDelivery;
  }

  // 비회원이 추가한 redis 캐싱 배송지 정보 가져와서 DB에 저장
  public Delivery saveAddedDelivery(PaymentsDTO paymentsDTO) {
    Long storeId = paymentsDTO.getStoreId();
    Long posId = paymentsDTO.getPosId();
    String compositeId = storeId + "-" + posId;
    System.out.println("compositeId = " + compositeId);
    List<Map<String, Object>> addedDelivery = deliveryRedisImplRepository.findAddedDelivery(compositeId);
    String serialNumber = makeSerialNumber();
    System.out.println("serialNumber = " + serialNumber);
    Delivery savedDelivery = null;
    for (Map<String, Object> addedAddress : addedDelivery) {
      Delivery delivery = Delivery.builder()
          .requestDeliveryTime((String) addedAddress.get("requestDeliveryTime"))
          .address((String) addedAddress.get("address"))
          .phoneNumber((String) addedAddress.get("phoneNumber"))
          .deliveryName((String) addedAddress.get("deliveryName"))
          .userName((String) addedAddress.get("name"))
          .requestInfo((String) addedAddress.get("requestInfo"))
          .postCode((String) addedAddress.get("postCode"))
          .deliveryType(DeliveryType.DELIVERY)
          .serialNumber(serialNumber)
          .deliveryStatus(DeliveryStatus.COMPLETE_PAYMENT)
          .build();

      System.out.println("delivery = " + delivery);
      savedDelivery = deliveryRepository.save(delivery);
      System.out.println("savedDelivery = " + savedDelivery);
    }
    return savedDelivery;
  }

  // 추가된 배송지 redis 캐싱, 회원일 경우 추가된 배송지 정보 delivery_list 테이블에 저장
  public DeliveryAddress addUserDeliveryAddress(DeliveryRedisAddRequestDTO deliveryRedisAddRequestDTO) {
    // 추가된 배송지 정보 redis 캐싱
    deliveryRedisRepository.saveDelivery(deliveryRedisAddRequestDTO);
    Long storeId = deliveryRedisAddRequestDTO.getStoreId();
    Long posId = deliveryRedisAddRequestDTO.getPosId();
    String compositeId = storeId + "-" + posId;
    // userId 찾기
    Long userId = cartRedisImplRepository.findUserId(compositeId);
    User user = userRepository.findById(userId).get();

    DeliveryAddress savedDeliveryAddress = null;
    // 회원이면 추가한 배송지 정보 DB에 저장
    if(userId != null) {
      DeliveryAddress deliveryAddress = DeliveryAddress.builder()
          .deliveryName(deliveryRedisAddRequestDTO.getDeliveryName())
          .phoneNumber(deliveryRedisAddRequestDTO.getPhoneNumber())
          .name(deliveryRedisAddRequestDTO.getUserName())
          .postCode(deliveryRedisAddRequestDTO.getPostCode())
          .address(deliveryRedisAddRequestDTO.getAddress())
          .user(user)
          .build();
      System.out.println("deliveryAddress = " + deliveryAddress);
      savedDeliveryAddress = deliveryAddressRepository.save(deliveryAddress);
    }
    return savedDeliveryAddress;
  }

}
