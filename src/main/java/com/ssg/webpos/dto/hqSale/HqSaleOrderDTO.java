package com.ssg.webpos.dto.hqSale;

import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.domain.enums.PayMethod;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HqSaleOrderDTO {
    private String serialNumber;
    private String storeName;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private PayMethod payMethod;
    private int totalPrice;
    private int pointUsePrice;
    private int couponUsePrice;
    private int finalTotalPrice;
    private int charge;
    private int totalOriginPrice;
    private int profit;
}
