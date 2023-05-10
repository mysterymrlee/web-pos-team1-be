package com.ssg.webpos.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class DeliveryDTO {
  private Long deliveryId;
  private String deliveryName;
  private String userName;
  private String phoneNumber;
  private String address;

  public DeliveryDTO(String deliveryName, String userName, String phoneNumber, String address) {
    this.deliveryName = deliveryName;
    this.userName = userName;
    this.phoneNumber = phoneNumber;
    this.address = address;
  }
}
