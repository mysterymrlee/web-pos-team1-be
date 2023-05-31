package com.ssg.webpos.dto.delivery;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class DeliveryRedisAddRequestDTO {
  private Long storeId;
  private Long posId;

  private String deliveryName;
  private String userName;
  private String address;
  private String phoneNumber;
  private String requestDeliveryTime;
  private String postCode;
  private byte isConfirmed;
}
