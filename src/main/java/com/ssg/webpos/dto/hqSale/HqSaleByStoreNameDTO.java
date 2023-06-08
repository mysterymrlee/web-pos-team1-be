package com.ssg.webpos.dto.hqSale;

import lombok.Data;

@Data
public class HqSaleByStoreNameDTO {
    private String storeName;
    private int settlementPrice;
}
