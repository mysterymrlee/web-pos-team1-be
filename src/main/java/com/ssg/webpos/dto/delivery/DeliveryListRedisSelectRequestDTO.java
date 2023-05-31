package com.ssg.webpos.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryListRedisSelectRequestDTO {
  private Long storeId;
  private Long posId;
  private String deliveryName;
  private String userName;
  private String address;
  private String postCode;
  private byte isDefault;
  private String requestDeliveryTime;
  private String requestInfo;
}
