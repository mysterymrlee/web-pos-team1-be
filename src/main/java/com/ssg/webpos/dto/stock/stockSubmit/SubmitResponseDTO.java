package com.ssg.webpos.dto.stock.stockSubmit;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubmitResponseDTO {
    private Long productRequestId;
    private int qty; // 발주 상품 신청 수량
    private Long productId;
    private int currentStock;
    private LocalDateTime createTime;
    private LocalDateTime lastModifiedTime;
}
