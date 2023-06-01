package com.ssg.webpos.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
  private LocalDateTime expiredDate;

  public PointSaveHistory(int pointSaveAmount, Order order, Point point) {
    super(); // BaseTime의 생성자 호출
    this.pointSaveAmount = pointSaveAmount;
    this.order = order;
    this.point = point;
    this.expiredDate = calculateExpirationDate();
  }
  // createDate + 24개월
  private LocalDateTime calculateExpirationDate() {
    return getCreatedDate().plusMonths(24);
  }

  public boolean isExpired() {
    LocalDateTime currentDate = LocalDateTime.now();
    return currentDate.isAfter(expiredDate);
  }
}
