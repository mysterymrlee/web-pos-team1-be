package com.ssg.webpos.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "stock_report")
@NoArgsConstructor
@AllArgsConstructor
// 주말 재고 리포트
public class StockReport extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "stock_report_id")
    private Long id;
    private int saturdayStockQty;
    private int sundayStockQty;

}
