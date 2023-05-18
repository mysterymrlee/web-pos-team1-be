package com.ssg.webpos.dto.delivery;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
// 배송지 목록에서 선택했을 때 요청되는 DTO
public class DeliveryRequestDTO {
  private Long storeId;
  private Long posId;
  private Long deliveryId;
  private String userName;
  private String phoneNumber;
  private String address;
  private String requestInfo;
  private String requestFinishedAt;
}
