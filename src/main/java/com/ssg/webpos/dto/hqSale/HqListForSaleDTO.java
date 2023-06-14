package com.ssg.webpos.dto.hqSale;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HqListForSaleDTO {
    private LocalDate settlementDate; // 정산일자
    private String storeName; // 가게 이름
    private int charge; // 수수료
    private int settlementPrice; // 정산금액
    private int originPrice; // 원가
    private int profit; // 이익
    // 어떤 제품이 팔렸는지 보는 것도 좋을 것 같은데
}
