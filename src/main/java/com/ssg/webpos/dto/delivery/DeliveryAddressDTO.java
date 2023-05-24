package com.ssg.webpos.dto.delivery;


import com.ssg.webpos.domain.DeliveryAddress;
import com.ssg.webpos.domain.PosStoreCompositeId;
import lombok.*;

import java.io.Serializable;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryAddressDTO implements Serializable {
  private PosStoreCompositeId posStoreCompositeId;
  private Long deliveryAddressId;
  private String address;
  private String phoneNumber;
  private String name;
  private String requestInfo;
  private String postCode;
  private boolean isDefault;
  private String deliveryName;

  public DeliveryAddressDTO(DeliveryAddress deliveryAddress) {
    this.deliveryAddressId = deliveryAddress.getId();
    this.address = deliveryAddress.getAddress();
    this.phoneNumber = deliveryAddress.getPhoneNumber();
    this.name = deliveryAddress.getName();
    this.requestInfo = deliveryAddress.getRequestInfo();
    this.postCode = deliveryAddress.getPostCode();
    this.isDefault = deliveryAddress.isDefault();
    this.deliveryName = deliveryAddress.getDeliveryName();
  }
}
