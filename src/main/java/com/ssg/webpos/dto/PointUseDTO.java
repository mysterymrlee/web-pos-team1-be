package com.ssg.webpos.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class PointUseDTO {
  private Long posId;
  private Long storeId;
  private int amount;
}
