package com.ssg.webpos.dto;

import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.Product;
import lombok.Data;

@Data
public class OrderProductDTO {
  private Long id;
  private int qty;
  private Product product;
  private Order order;
}
