package com.ssg.webpos.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class CouponAddResponseDTO {
  private int deductedPrice;

  public CouponAddResponseDTO(int deductedPrice) {
    this.deductedPrice = deductedPrice;
  }
}
