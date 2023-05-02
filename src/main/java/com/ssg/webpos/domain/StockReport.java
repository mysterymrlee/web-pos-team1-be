package com.ssg.webpos.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Table(name = "stock_report")
@AllArgsConstructor
@NoArgsConstructor
// 주말 재고 리포트
public class StockReport extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "stock_report_id")
    private Long id;
    private int saturdayStockQty;
    private int sundayStockQty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;
}
