package com.ssg.webpos.dto.settlement;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SettlementMonthReportDTO {
    private Long settlementMontnId;
    private int settlementPrice;
    private String settlementDate; //yyyy-mm 형식으로 변환하기 위해 LocalDate에서 String으로 변환
    private Long storeId;

    private String storeName;
    private LocalDateTime createdDate;
}
