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
    private int productStock;
    private int salePrice;
}
