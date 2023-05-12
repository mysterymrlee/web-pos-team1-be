package com.ssg.webpos.dto;

import com.ssg.webpos.domain.enums.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SettlementMonthDetailResponseDTO {
    private LocalDateTime orderDate;
    private OrderStatus orderState;
    private PayMethod paymentMethod;
    private int totalPrice;
    private int finalTotalPrice;

    //사용자 정보
    private LocalDate userBirth;
    private RoleUser roleUser;

    //배송 상태
    private DeliveryType deliveryType;
    private DeliveryStatus deliveryStatus;


}
