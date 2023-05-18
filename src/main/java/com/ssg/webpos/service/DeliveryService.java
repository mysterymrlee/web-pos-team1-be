package com.ssg.webpos.service;

import com.ssg.webpos.domain.Delivery;
import com.ssg.webpos.domain.DeliveryAddress;
import com.ssg.webpos.domain.User;
import com.ssg.webpos.domain.enums.DeliveryStatus;
import com.ssg.webpos.dto.delivery.DeliveryAddDTO;
import com.ssg.webpos.dto.delivery.DeliveryAddressDTO;
import com.ssg.webpos.repository.UserRepository;
import com.ssg.webpos.repository.delivery.DeliveryAddressRepository;
import com.ssg.webpos.repository.delivery.DeliveryRedisImplRepository;
import com.ssg.webpos.repository.delivery.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryService {
  private final DeliveryRepository deliveryRepository;
  private final DeliveryRedisImplRepository deliveryRedisImplRepository;
  private final UserRepository userRepository;
  private final DeliveryAddressRepository deliveryAddressRepository;

  // 문자열을 LocalDateTime으로 파싱
  public LocalDateTime LocalDateParse(String requestFinishedAt) throws DateTimeParseException {
    LocalDateTime dateTime = LocalDateTime.parse(requestFinishedAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    System.out.println("LocalDateTime = " + dateTime); // 2023-05-12T18:00:00
    return dateTime;
  }

  // 배송지 추가
  @Transactional
  public void addDeliveryAddress(DeliveryAddDTO deliveryDTO) {
    LocalDateTime requestFinishedAt = LocalDateParse(deliveryDTO.getRequestFinishedAt());
    Delivery delivery = Delivery.builder()
        .deliveryName(deliveryDTO.getDeliveryName())
        .userName(deliveryDTO.getUserName())
        .address(deliveryDTO.getAddress())
        .phoneNumber(deliveryDTO.getPhoneNumber())
        .finishedDate(requestFinishedAt)
        .requestInfo(deliveryDTO.getRequestInfo())
        .deliveryStatus(DeliveryStatus.PROCESS)
        .deliveryType(deliveryDTO.getDeliveryType())
        .startedDate(LocalDateTime.now())
        .build();

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

    // deliveryList에 저장된 해당 유저의 배송지 목록을 가져와서 DTO로 변환
    List<DeliveryAddressDTO> deliveryAddressList =
        user.getDeliveryAddressList().stream()
            .map(da -> new DeliveryAddressDTO(da))
            .collect(Collectors.toList());

    System.out.println("deliveryAddressList = " + deliveryAddressList);

    return deliveryAddressList;
  }

  // 유저의 배송지 목록에서 배송지 선택
  public DeliveryAddressDTO getSelectedDeliveryAddress(Long id) {
    DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(id).get();
    System.out.println("deliveryAddress = " + deliveryAddress);
    return new DeliveryAddressDTO(deliveryAddress);
  }
}
