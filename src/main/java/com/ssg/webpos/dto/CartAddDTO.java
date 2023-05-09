package com.ssg.webpos.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class CartAddDTO implements Serializable {
  private Long posId;
  private Long productId;
  private int qty;

  public CartAddDTO(Long posId, Long productId, int qty) {
    this.posId = posId;
    this.productId = productId;
    this.qty = qty;
  }
}