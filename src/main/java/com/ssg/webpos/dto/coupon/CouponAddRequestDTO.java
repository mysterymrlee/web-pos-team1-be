package com.ssg.webpos.dto.coupon;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class CouponAddRequestDTO {
  private Long storeId;
  private Long posId;
  private String serialNumber;
}
