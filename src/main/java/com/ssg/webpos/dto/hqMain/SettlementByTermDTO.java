package com.ssg.webpos.dto.hqMain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SettlementByTermDTO {
    // 주문수, 매출액, 수수료, 영업이익
    private int orderCount; // 주문수
    private int settlementPrice; // 매출액
    private int charge; // 수수료
    private int profit; // 영업이익
    private LocalDate startDate; // 시작날짜
    private LocalDate endDate; //종료 날짜, 어제는 종료날짜가 null
}
