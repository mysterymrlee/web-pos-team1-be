package com.ssg.webpos.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "product")
@AllArgsConstructor
@NoArgsConstructor
@ToString(of={"id","productCode","name","salePrice","imageUrl","description","category","stock"})
public class Product extends BaseTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
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
  private String category;
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
  private Event event;

  @OneToMany(mappedBy = "product")
  private List<Cart> cartList = new ArrayList<>();

  public void plusStockQuantity(int qty) {
    this.stock += qty;
  }

  // CartService - addOrder에서 재고 부족 체크함
  public void minusStockQuantity(int qty) {
    this.stock -= qty;
  }

}
