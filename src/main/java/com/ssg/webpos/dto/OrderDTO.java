package com.ssg.webpos.dto;

import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.domain.enums.PayMethod;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderDTO {
  private Long id;
  private LocalDateTime orderDate;
  private OrderStatus orderStatus;
  private PayMethod payMethod;
  private int totalPrice, totalQuantity, finalTotalPrice;
}
