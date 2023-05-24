package com.ssg.webpos.dto.delivery;

import com.ssg.webpos.domain.DeliveryAddress;
import com.ssg.webpos.domain.PosStoreCompositeId;
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
  private PosStoreCompositeId posStoreCompositeId;
  private String deliveryName;
  private String userName;
  private String address;
  private String postCode;
  private boolean isDefault;
  private String requestDeliveryTime;
  private String requestInfo;
}
