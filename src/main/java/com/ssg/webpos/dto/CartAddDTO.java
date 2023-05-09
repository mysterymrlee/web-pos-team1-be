package com.ssg.webpos.dto;

import com.ssg.webpos.domain.PosStoreCompositeId;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class CartAddDTO implements Serializable {
  private PosStoreCompositeId posStoreCompositeId;
  private Long productId;
  private int qty;

  public CartAddDTO(PosStoreCompositeId posStoreCompositeId, Long productId, int qty) {
    this.posStoreCompositeId = posStoreCompositeId;
    this.productId = productId;
    this.qty = qty;
  }
}