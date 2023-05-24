package com.ssg.webpos.dto.delivery;

import lombok.Data;

import java.util.List;

@Data
public class DeliveryListRedisSelectRequestDTO {
  private Long storeId;
  private Long posId;
  private List<DeliveryListRedisSelectDTO> selectedDeliveryAddress;
}
