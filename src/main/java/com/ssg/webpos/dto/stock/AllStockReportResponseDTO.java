package com.ssg.webpos.dto.stock;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AllStockReportResponseDTO {
    // 발주 신청 시간, 제품 이름, 제품 카테고리, 제품 가격, 매장 이름, 현재 재고, 발주 주문 수량
    private LocalDateTime lastModifiedDate;
    private String productName;
    private String productCategory;
    private int productSalePrice;
    private String storeName;
    private int currentStock;

}
