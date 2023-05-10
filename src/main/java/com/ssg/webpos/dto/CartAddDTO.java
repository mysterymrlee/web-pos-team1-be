package com.ssg.webpos.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString
public class CartAddDTO implements Serializable {
  private Long posId;
  @NotEmpty
  private Long productId;
  private int cartQty;

  public CartAddDTO(Long posId, Long productId, int cartQty) {
    this.posId = posId;
    this.productId = productId;
    this.cartQty = cartQty;
  }
}