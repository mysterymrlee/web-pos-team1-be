package com.ssg.webpos.dto.hqMain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class AllAndStoreDTO {
    // 모든 백화점의 정산합, 가게별 리스트
    private int allYesterdaySettlementDayPrice;
    private List<StoreDTO> storeDTO = new ArrayList<>();

}
