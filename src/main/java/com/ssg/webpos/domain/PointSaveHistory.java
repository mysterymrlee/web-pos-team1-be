package com.ssg.webpos.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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

  public PointSaveHistory(int amount, User user, Order order) {
    this.amount = amount;
    this.user = user;
    this.order = order;
  }

  @NotNull
  private int amount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private Order order;
}
