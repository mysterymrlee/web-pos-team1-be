package com.ssg.webpos.dto.cartDto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString
public class CartRedisAddDTO implements Serializable {
  private Long productId;
  private int cartQty;


  public CartRedisAddDTO(CartAddDTO cartAddDTO) {
    this.productId = cartAddDTO.getProductId();
    this.cartQty = cartAddDTO.getCartQty();
  }

}