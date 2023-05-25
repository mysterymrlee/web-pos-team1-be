package com.ssg.webpos.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class TestDTO {
  private Long productId;
  private int cartQty;
}
