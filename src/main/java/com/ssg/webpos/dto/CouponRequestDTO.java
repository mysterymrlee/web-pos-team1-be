package com.ssg.webpos.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class CouponRequestDTO {
  private Long storeId;
  private Long posId;
  private String serialNumber;
}
