package com.ssg.webpos.dto;

import com.ssg.webpos.domain.Delivery;
import com.ssg.webpos.domain.enums.DeliveryStatus;
import com.ssg.webpos.domain.enums.DeliveryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryCheckResponseDTO {
  private String serialNumber;
  private DeliveryStatus deliveryStatus;
  private String address;
  private DeliveryType deliveryType;
  private String phoneNumber;
  private String deliveryName;
  private String userName;
  private String requestInfo;
  private String requestDeliveryTime;
  private LocalDateTime startedDate;
  private LocalDateTime finishedDate;
  private String msg;

  public DeliveryCheckResponseDTO(Delivery findDelivery, String msg) {
    this.serialNumber = findDelivery.getSerialNumber();
    this.deliveryStatus = findDelivery.getDeliveryStatus();
    this.address = findDelivery.getAddress();
    this.deliveryType = findDelivery.getDeliveryType();
    this.phoneNumber = findDelivery.getPhoneNumber();
    this.deliveryName = findDelivery.getDeliveryName();
    this.userName = findDelivery.getUserName();
    this.requestInfo = findDelivery.getRequestInfo();
    this.requestDeliveryTime = findDelivery.getRequestDeliveryTime();
    this.startedDate = findDelivery.getStartedDate();
    this.finishedDate = findDelivery.getFinishedDate();
    this.msg = msg;
  }
}
