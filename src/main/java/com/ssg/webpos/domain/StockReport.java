package com.ssg.webpos.domain;

import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Table(name = "stock_report")
@AllArgsConstructor
@NoArgsConstructor
@ToString
// 주말 재고 리포트
public class StockReport extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_report_id")
    private Long id;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;
    private boolean isSubmit;
    private int currentStock;

    // ProductRequest처럼 양방형관계를 지닌 것 만들기
    // ProductRequest에 사장님 코드 참고해서 작성함
    // Product prodcut 객체로 해당 객체의 재고 수량 조회
    public void addProductWithAssociation(Product product) {
        this.product = product;
        product.getStock();
    }

    // 특정 product 를 가지고 있는 StockReport 조회
    // Jpa로 객체 조회할 수 있는지 확인
}
