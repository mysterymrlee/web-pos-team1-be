package com.ssg.webpos.dto.order;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Data
public class OrderDetailResponseDTO {
    private String merchantUid;
    private LocalDateTime orderDate; //주문일자
    private int totalPrice; //총합계
    private int couponUsePrice; //쿠폰사용금액
    private int pointUsePrice; // 포인트사용금액, PointUsesHistory에서 amount를 가져온다.
    private int finalTotalPrice; // 최총합금액
    private String storeName;
    private String storeAdress;
    private String userName; // 없을 경우 빈칸
    private int userPoint; // 없을 경우 0원
    private int productPrice; // 과세물품가액, 적절한 영어 단어가 없어서 임의로 설정, finalTotalPrice의 10/11
    private int vat; // 부가세, finalTotalPrice의 1/11
    // finalTotalPrice를 받으면 해당 매서드에서 활용해서 DTO.setProductPrice(); 처럼 작성할 예정
    private List<OrderDetailProductResponseDTO> orderDetailProductResponseDTOList = new ArrayList<>(); // 주문상품

}
