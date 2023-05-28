package com.ssg.webpos.dto.delivery;

import com.ssg.webpos.domain.enums.DeliveryStatus;
import com.ssg.webpos.domain.enums.DeliveryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryAddDTO implements Serializable {
  private String deliveryName;
  private String userName;
  private String address;
  private String phoneNumber;
  private String requestInfo;
  private DeliveryStatus deliveryStatus;
  private DeliveryType deliveryType;
  private String requestDeliveryTime;
  private String serialNumber;
  private String postCode;
}
