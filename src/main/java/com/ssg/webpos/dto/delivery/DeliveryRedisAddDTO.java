package com.ssg.webpos.dto.delivery;

import com.ssg.webpos.domain.PosStoreCompositeId;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class DeliveryRedisAddDTO implements Serializable {
  private PosStoreCompositeId posStoreCompositeId;
  private String deliveryName;
  private String userName;
  private String address;
  private String phoneNumber;
  private String requestInfo;
  private String requestDeliveryTime;
}
