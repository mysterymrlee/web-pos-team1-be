package com.ssg.webpos.dto.stock.stockSubmit;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubmitRequestDTO {
    private Long stockReportId;
    private int qty;
    private Long productId;
    private int currentStock;
}
