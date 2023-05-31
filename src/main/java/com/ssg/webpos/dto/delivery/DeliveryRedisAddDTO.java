package com.ssg.webpos.dto.delivery;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class DeliveryRedisAddDTO implements Serializable {
  private String deliveryName;
  private String userName;
  private String address;
  private String phoneNumber;
  private String requestDeliveryTime;
  private String postCode;
}
