package com.ssg.webpos.dto.hqStock;

import lombok.Data;

@Data
public class StockReportResponseDTO {
    private String productCode;
    private String storeName;
    private String category;
    private String productName;
    private int stock; // 재고 수량
    private int salePrice;
    private int originPrice;
    private byte saleState; // 판매여부
}
