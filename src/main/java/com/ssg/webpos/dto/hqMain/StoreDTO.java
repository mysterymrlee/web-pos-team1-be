package com.ssg.webpos.dto.hqMain;

import lombok.Data;

@Data
public class StoreDTO {
    // 가게 이름, 어제 정산합
    private String storeName;
    private int storeSettlementDayPrice;
}
