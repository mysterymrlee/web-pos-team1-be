package com.ssg.webpos.dto.order;

import lombok.Data;

@Data
public class OrderDetailProductResponseDTO {
    private int productQty;
    private String productName;
    private int productSalePrice;
    //
    private int cartQty;
    private int originPrice;
}
