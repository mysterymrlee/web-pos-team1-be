package com.ssg.webpos.dto.order;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Data
public class OrderDetailResponseDTO {
    private String serialNumber; //주문일렬번호
    private LocalDateTime orderDate; //주문일자
    private int totalPrice; //총합계
    private int couponUsePrice; //쿠폰사용금액
    private int pointUsePrice; // 포인트사용금액, PointUsesHistory에서 amount를 가져온다.
    private int finalTotalPrice; // 최총합금액

    private List<OrderDetailProductResponseDTOList> orderDetailProductResponseDTOList = new ArrayList<>(); // 주문상품

}
