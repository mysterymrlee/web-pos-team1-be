package com.ssg.webpos.domain;

import lombok.*;
import lombok.experimental.Accessors;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Table(name = "stock_report")
@AllArgsConstructor
@NoArgsConstructor
@ToString(of = {"id","isSubmit","currentStock"})
// 주말 재고 리포트
public class StockReport extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_report_id")
    private Long id;
//    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

//    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;
    private boolean isSubmit; //변수명은 명사로, 상태니까 플래그
    private int currentStock;
    public void addProductWithAssociation(Product product) {
        this.product = product;
        product.getStock();
    }



}
