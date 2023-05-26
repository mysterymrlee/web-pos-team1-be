package com.ssg.webpos.dto.order;

import lombok.Data;

@Data
public class OrderDetailProductResponseDTOList {
    private int productQty;
    private String productName;
    private int productSalePrice;
}
