package com.ssg.webpos.dto.hqMain;

import lombok.Data;

@Data
public class SettlementByTermListDTO {
    // 어제
    private SettlementByTermDTO yesterdayDTO;
    // 이번주
    private SettlementByTermDTO thisWeekDTO;
    // 이번달
    private SettlementByTermDTO thisMonthDTO;
    // 올해
    private SettlementByTermDTO thisYearDTO;
}
