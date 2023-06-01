package com.ssg.webpos.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "point_use_history")
public class PointUseHistory extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_use_history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_id")
    private Point point;

    private byte pointStatus; // 0: 적립, 1: 취소
    private int pointUseAmount;

    public PointUseHistory(int pointUseAmount, Order order) {
        this.pointUseAmount = pointUseAmount;
        this.order = order;
        this.point = point;
    }
}
