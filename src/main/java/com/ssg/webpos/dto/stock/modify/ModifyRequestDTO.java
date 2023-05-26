package com.ssg.webpos.dto.stock.modify;

import lombok.Data;

@Data
public class ModifyRequestDTO {
    private String storeId;
    private Long stockReportId;
    private int currentStock;
}
