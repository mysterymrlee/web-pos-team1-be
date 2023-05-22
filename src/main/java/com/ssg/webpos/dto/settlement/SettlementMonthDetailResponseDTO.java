package com.ssg.webpos.dto.settlement;

import com.ssg.webpos.domain.Order;
import com.ssg.webpos.domain.enums.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class SettlementMonthDetailResponseDTO {
    private LocalDateTime orderDate;
    private OrderStatus orderState;
    private PayMethod paymentMethod;
    private int totalPrice;
    private int finalTotalPrice;


    //배송 상태
    private DeliveryType deliveryType;
    private DeliveryStatus deliveryStatus;

    public SettlementMonthDetailResponseDTO(Order order) {
        this.orderDate = order.getOrderDate();
        this.orderState = order.getOrderStatus();
        this.paymentMethod = order.getPayMethod();
        this.totalPrice = order.getTotalPrice();
        this.deliveryType = order.getDelivery().getDeliveryType();
        this.deliveryStatus = order.getDelivery().getDeliveryStatus();
    }


}
