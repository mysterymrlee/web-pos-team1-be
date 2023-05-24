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
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@ToString
@Table(name = "orders")
public class Order extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;
    private String serialNumber;

    // pos
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "pos_id", referencedColumnName = "pos_id"),
            @JoinColumn(name = "store_id", referencedColumnName = "store_id")
    })
    private Pos pos;

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

    @OneToMany(mappedBy = "order")
    private List<Cart> cartList = new ArrayList<>();

    public static Order createOrder(Pos pos) {
        Order order = new Order();
        order.setPos(pos);
        return order;
    }

    public void changeTotalPrice(int updatePrice) {
        this.totalPrice += updatePrice;
    }

    public void minusTotalPrice(int updatePrice) {
        this.totalPrice -= updatePrice;
    }

public Order(OrderStatus orderStatus, PayMethod payMethod, int totalPrice) {
        this.orderStatus = orderStatus;
        this.payMethod = payMethod;
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderStatus=" + orderStatus +
                ", payMethod=" + payMethod +
                ", totalPrice=" + totalPrice +
                ", finalTotalPrice=" + finalTotalPrice +
                '}';
    }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) &&
                Objects.equals(pos, order.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pos);
    }
    private int totalOriginPrice;
    private void calcProfit(int finalTotalPrice, int totalOriginPrice) {
        int calculateProfit = finalTotalPrice - totalOriginPrice;
        this.profit = calculateProfit;
    }
    private int profit;

}
//Order order = new Order();
//// order.setProfit(dto.getFinalTotalPrice - dto.getTotalOriginPrice);
//order.calcProfit(dto.getFinalTotalPrice, dto.getTotalOriginPrice)
