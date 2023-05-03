package com.ssg.webpos.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "cart")
@AllArgsConstructor
@NoArgsConstructor
public class Cart extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;
    
    private int qty; // 주문 수량

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    public static Cart createOrderProduct(Order order, Product product, int qty) {
        Cart cart = new Cart();
        cart.setOrder(order);
        cart.setProduct(product);
        cart.setQty(qty);
        return cart;
    }

    // 이미 담겨있는 물건을 또 담을 경우 수량만 증가
    public void addQty(int qty) {
        this.qty += qty;
    }

    public void setOrder(Order order) {
        this.order = order;
        order.getOrderProductList().add(this);
    }

    public Cart(Product product, Order order) {
        this.product = product;
        this.order = order;
    }
}
