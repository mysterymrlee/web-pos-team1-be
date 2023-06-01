package com.ssg.webpos.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "point")
public class Point extends BaseTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "point_id")
  private Long id;
  private int pointAmount;
  @OneToOne
  private User user;
  @OneToMany(mappedBy = "point")
  private List<PointUseHistory> pointUseHistoryList = new ArrayList<>();
  @OneToMany(mappedBy = "point")
  private List<PointSaveHistory> pointSaveHistoryList = new ArrayList<>();
}
