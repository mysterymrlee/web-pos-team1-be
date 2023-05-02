package com.ssg.webpos.dto;

import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.Product;
import lombok.Data;

@Data
public class CartAddDTO {
  private Long posId;
  private Long productId;
  private int qty;

  public CartAddDTO(Long posId, Long productId, int qty) {
    this.posId = posId;
    this.productId = productId;
    this.qty = qty;
  }
}
