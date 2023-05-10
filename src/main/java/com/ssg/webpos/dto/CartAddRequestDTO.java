package com.ssg.webpos.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
@Data
@NoArgsConstructor
@ToString
public class CartAddRequestDTO {
  private List<CartAddDTO> cartItemList;
}
