package com.ssg.webpos.dto.settlement;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class RequestSettlementDayRangeByStoreIdDTO {
    private String startDate;
    private String endDate;
    private Long storeId;
}
