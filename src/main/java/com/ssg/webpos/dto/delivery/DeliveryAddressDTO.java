package com.ssg.webpos.dto.delivery;


import com.ssg.webpos.domain.DeliveryAddress;
import lombok.*;

import java.io.Serializable;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryAddressDTO implements Serializable {
  private Long deliveryAddressId;
  private String address;
  private String phoneNumber;
  private String userName;
  private String requestInfo;
  private String postCode;
  private byte isDefault;
  private String deliveryName;

  public DeliveryAddressDTO(DeliveryAddress deliveryAddress) {
    this.deliveryAddressId = deliveryAddress.getId();
    this.address = deliveryAddress.getAddress();
    this.phoneNumber = deliveryAddress.getPhoneNumber();
    this.userName = deliveryAddress.getName();
    this.requestInfo = deliveryAddress.getRequestInfo();
    this.postCode = deliveryAddress.getPostCode();
    this.isDefault = deliveryAddress.getIsDefault();
    this.deliveryName = deliveryAddress.getDeliveryName();
  }
}
