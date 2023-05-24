package com.ssg.webpos.dto.stock.stockSubmit;

import com.ssg.webpos.domain.Product;
import com.ssg.webpos.domain.Store;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseForDBDTO {
    private Long productRequestId;
    private int qty; // 발주 상품 신청 수량
    private Product product;
    private Store store;
    private int currentStock;
    private LocalDateTime createTime;
    private LocalDateTime lastModifiedTime;

}
