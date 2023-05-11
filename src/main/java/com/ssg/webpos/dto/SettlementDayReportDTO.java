package com.ssg.webpos.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SettlementDayReportDTO {
    private Long settlementDayId;
    private int settlementPrice;
    private LocalDate settlementDate;

    private Long storeId;
    private LocalDateTime createdDate;
}
