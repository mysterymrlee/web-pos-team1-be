package com.ssg.webpos.dto.settlement;

import lombok.Data;

@Data
public class SettlementMonthDetailRequestDTO {
    // 요청하는 날짜와 store_id

    private Long store_id;
    private String date;
}
