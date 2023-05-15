package com.ssg.webpos.dto;

import com.ssg.webpos.domain.DeliveryAddress;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class DeliveryAddressListDTO {
  private Long deliveryAddressId;
  private Long userId;
  private String address;
  private String phoneNumber;
  private String name;
  private String requestInfo;
  private List<DeliveryAddress> deliveryAddressList = new ArrayList<>();
}
