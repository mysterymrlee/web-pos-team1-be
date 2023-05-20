package com.ssg.webpos.dto;

import com.ssg.webpos.domain.enums.CouponStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@ToString
public class CouponDTO {
  private Long storeId;
  private Long posId;
  private Long couponId;
  private String name;
  private CouponStatus couponStatus;
  private int deductedPrice;
  private String serialNumber;
  private LocalDate expiredDate;

}
