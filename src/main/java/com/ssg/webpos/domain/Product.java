package com.ssg.webpos.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "product")
public class Product extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_id")
    private Long id;
    @NotNull
    private String productCode;
    @NotNull
    private String name;
    @NotNull
    private int salePrice; // 판매가
    private String imageUrl;
    private String description;
    @NotNull
    @Column(columnDefinition = "integer default 0")
    private int stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToMany(mappedBy = "product")
    private List<StockReport> stockReportList = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<ProductRequest> productRequestList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @Column(nullable = true)
    private Event event;

    @OneToMany(mappedBy = "order_product")
    private List<OrderProduct> orderProductList = new ArrayList<>();
}
