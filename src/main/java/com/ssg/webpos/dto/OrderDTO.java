package com.ssg.webpos.dto;

import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.domain.enums.PayMethod;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderDTO {
  private Long orderId;
  private Long posId;
  private Long storeId;
  private LocalDateTime orderDate;
  private OrderStatus orderStatus;
  private PayMethod payMethod;
  private String serialNumber;
  private int totalPrice;
  private int totalQuantity;
  private int finalTotalPrice;

  public OrderDTO(OrderStatus orderStatus, PayMethod payMethod, int totalQuantity) {
    this.orderStatus = orderStatus;
    this.payMethod = payMethod;
    this.totalQuantity = totalQuantity;
  }
}
