package com.ssg.webpos.dto.settlement;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RequestSettlementMonthRangeDTO {
    //"yyyy-mm" 형식으로 받는다.
    private String startDate;
    private String endDate;
}
