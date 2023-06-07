package com.ssg.webpos.dto.hqSale;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HqSettlementDayDTO {
    private LocalDate settlementDayDate;
    private int settlementDaySettlementPrice;
}
