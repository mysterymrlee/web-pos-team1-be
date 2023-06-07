package com.ssg.webpos.dto.hqStock;

import lombok.Data;

@Data
public class StockReportUpdateRequestDTO {
    private int prdouctId; // 무조건 있어야한다. // 활용할 때는 Long으로 타입 바꾸기
    private String productName;
    private int stock; // 재고수량
    private int salePrice; // 판매가격
    private int originPrice; // 원가
    private int saleState; // 판매여부 // 활용할 때는 byte로 타입 바꾸기
}
