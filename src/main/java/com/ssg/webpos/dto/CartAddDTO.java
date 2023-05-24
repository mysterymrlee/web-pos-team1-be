package com.ssg.webpos.dto;

import com.ssg.webpos.domain.Cart;
import com.ssg.webpos.domain.PosStoreCompositeId;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString
public class CartAddDTO implements Serializable {
  private PosStoreCompositeId posStoreCompositeId;
  private Long productId;
  private int cartQty;

  public CartAddDTO(Long posId, Long storeId, Long productId, int cartQty) {
    this.posStoreCompositeId = new PosStoreCompositeId(posId, storeId);
    this.productId = productId;
    this.cartQty = cartQty;
  }

  }
