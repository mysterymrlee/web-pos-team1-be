package com.ssg.webpos.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name = "product_request")
@NoArgsConstructor
@AllArgsConstructor
// 상품 발주 신청
public class ProductRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_request_id")
    private Long id;

    @NotNull
    private int qty; // 발주 상품 신청 수량

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

}
