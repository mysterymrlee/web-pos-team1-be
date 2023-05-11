package com.ssg.webpos.dto;

import com.ssg.webpos.domain.PosStoreCompositeId;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString
public class DeliveryDTO implements Serializable {
  private PosStoreCompositeId posStoreCompositeId;
  private String deliveryName;
  private String userName;
  private String phoneNumber;
  private String address;
}
