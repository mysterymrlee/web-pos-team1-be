package com.ssg.webpos.domain;

import com.ssg.webpos.domain.enums.OrderStatus;
import com.ssg.webpos.domain.enums.PayMethod;
import com.ssg.webpos.dto.CartAddDTO;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@ToString
@Table(name = "orders")
public class Order extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_id")
    private Long id;
    private LocalDateTime orderDate;
    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @NotNull
    @Enumerated(EnumType.STRING)
    private PayMethod payMethod;
    private int totalPrice;
    @NotNull
    private int totalQuantity; // 총 주문 수량
    private int couponUsePrice; // 쿠폰 사용 금액
    private int finalTotalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "order")
    private List<PointHistory> pointHistoryList = new ArrayList<>();
    @OneToOne
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;
    @OneToMany(mappedBy = "order")
    private List<Coupon> couponList = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;
    @OneToMany(mappedBy = "order")
    private List<SettlementDay> settlementDayList = new ArrayList<>();
    @OneToMany(mappedBy = "order")
    private List<Cart> orderProductList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pos_id")
    private Pos pos;

    public static Order createOrder(Pos pos) {
        Order order = new Order();
        order.setPos(pos);
        return order;
    }

    public void changeTotalPrice(int updatePrice) {
        this.totalPrice += updatePrice;
    }

    public Order(OrderStatus orderStatus, PayMethod payMethod, int totalPrice) {
        this.orderStatus = orderStatus;
        this.payMethod = payMethod;
        this.totalPrice = totalPrice;
    }
}
