package com.ssg.webpos.dto.settlement;

import com.ssg.webpos.domain.SettlementMonth;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SettlementMonthReportDTO {
    // settlementDate를 String에서 LocalDate로 변경, "yyyy-mm" 조회하는 거 수정
    // LocalDate 타입인 settlementDate에서 year 찾는 것으로 변경
    private Long settlementMontnId;
    private int settlementPrice;
//    private String settlementDate; //yyyy-mm 형식으로 변환하기 위해 LocalDate에서 String으로 변환
    private LocalDate settlementDate;
    private Long storeId;

    private String storeName;
    private LocalDateTime createdDate;

    public SettlementMonthReportDTO(SettlementMonth settlementMonth) {
        this.settlementMontnId = settlementMonth.getId();
        this.settlementPrice = settlementMonth.getSettlementPrice();
        this.settlementDate = settlementMonth.getSettlementDate();
        this.storeId = settlementMonth.getStore().getId();
        this.storeName = settlementMonth.getStore().getName();
        this.createdDate = settlementMonth.getCreatedDate();

    }
}
