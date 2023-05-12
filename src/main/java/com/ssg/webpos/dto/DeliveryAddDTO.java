package com.ssg.webpos.dto;

import com.ssg.webpos.domain.PosStoreCompositeId;
import com.ssg.webpos.domain.enums.DeliveryStatus;
import com.ssg.webpos.domain.enums.DeliveryType;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class DeliveryAddDTO implements Serializable {
  private PosStoreCompositeId posStoreCompositeId;
  private String deliveryName;
  private String userName;
  private String address;
  private String phoneNumber;
  private String requestFinishedAt; // yyyy-MM-ddTHH:mm:ss
  private String requestInfo;
  private DeliveryType deliveryType;
}
