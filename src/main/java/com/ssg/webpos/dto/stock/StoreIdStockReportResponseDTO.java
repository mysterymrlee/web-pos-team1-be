package com.ssg.webpos.dto.stock;

import lombok.Data;

@Data
public class StoreIdStockReportResponseDTO {
    private int currentStock;
    private boolean isSubmit;
    private String productName;
    private int productSalePrice;
    private String category;
}
