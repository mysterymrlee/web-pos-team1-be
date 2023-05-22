package com.ssg.webpos.dto.delivery;

import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.PosStoreCompositeId;
import com.ssg.webpos.domain.enums.DeliveryStatus;
import com.ssg.webpos.domain.enums.DeliveryType;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class DeliveryAddDTO implements Serializable {
  private Long deliveryId;
  private Long posId;
  private Long storeId;
  private String serialNumber;
  private String deliveryName;
  private String userName;
  private String address;
  private String phoneNumber;
  private String requestFinishedAt; // yyyy-MM-ddTHH:mm:ss
  private String requestInfo;
  private DeliveryType deliveryType;
}
