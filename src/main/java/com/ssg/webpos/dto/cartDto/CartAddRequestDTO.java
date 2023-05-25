package com.ssg.webpos.dto.cartDto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.util.List;
@Data
@NoArgsConstructor
@ToString
public class CartAddRequestDTO {
  private Long posId;
  private Long storeId;
  private int totalPrice;
  private int totalOriginPrice;
  private String orderName;
  @NotEmpty
  private List<CartAddDTO> cartItemList;
}
