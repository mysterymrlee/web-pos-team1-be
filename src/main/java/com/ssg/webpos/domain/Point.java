package com.ssg.webpos.domain;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"pointUseHistoryList", "pointSaveHistoryList"})
@Table(name = "point")
public class Point extends BaseTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "point_id")
  private Long id;
  @ColumnDefault("0")
  private int pointAmount;
  @OneToOne(mappedBy = "point")
  private User user;
  @OneToMany(mappedBy = "point")
  private List<PointUseHistory> pointUseHistoryList = new ArrayList<>();
  @OneToMany(mappedBy = "point")
  private List<PointSaveHistory> pointSaveHistoryList = new ArrayList<>();
}
