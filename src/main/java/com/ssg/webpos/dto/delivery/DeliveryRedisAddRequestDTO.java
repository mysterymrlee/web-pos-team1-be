package com.ssg.webpos.dto.delivery;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString

public class DeliveryRedisAddRequestDTO {
  private Long storeId;
  private Long posId;
  private List<DeliveryRedisAddDTO> deliveryAddList;
}
