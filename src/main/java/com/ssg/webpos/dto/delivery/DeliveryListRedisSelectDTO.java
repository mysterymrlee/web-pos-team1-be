package com.ssg.webpos.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryListRedisSelectDTO implements Serializable {
  private String deliveryName;
  private String userName;
  private String address;
  private String postCode;
  private boolean isDefault;
  private String requestDeliveryTime;
  private String requestInfo;
}
