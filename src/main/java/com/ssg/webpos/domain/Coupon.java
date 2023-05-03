package com.ssg.webpos.domain;

import com.ssg.webpos.domain.enums.CouponStatus;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "coupon")
public class Coupon extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long id;
    @NotNull
    private String serialNumber;
    @NotNull
    private String name;
    private String content;
    @NotNull
    private int deductedPrice;
    @NotNull
    private LocalDate expiredDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CouponStatus couponStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
