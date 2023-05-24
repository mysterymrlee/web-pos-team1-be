package com.ssg.webpos.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class StockReportDTO {
    private String storeName;
    private String productName;
    private int currentStock; // 현재 재고 수량
    private int salePrice;
     // 발주 신청 수량

}
