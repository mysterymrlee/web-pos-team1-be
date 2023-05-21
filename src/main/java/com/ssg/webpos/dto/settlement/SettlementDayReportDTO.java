package com.ssg.webpos.dto.settlement;

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
    private String storeName;
    private LocalDateTime createdDate;
}
