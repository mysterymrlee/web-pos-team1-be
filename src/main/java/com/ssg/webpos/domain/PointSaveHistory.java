package com.ssg.webpos.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "point_save_history")
public class PointSaveHistory extends BaseTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "point_save_history_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private Order order;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "point_id")
  private Point point;
  private byte pointStatus; // 0: 적립, 1: 취소
  private int pointSaveAmount;
  private LocalDate expiredDate;

  public PointSaveHistory(int pointSaveAmount, Order order) {
    this.pointSaveAmount = pointSaveAmount;
    this.order = order;
  }
}
