package com.ssg.webpos.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "event")
@AllArgsConstructor
@NoArgsConstructor
public class Event extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "event_id")
    private Long id;
    private String name;
    private Double discountRate;

    private LocalDate startDate;
    private LocalDate endDate;
    @Column(columnDefinition = "integer default 0")
    private int eventStatus; // 0: 진행x, 1: 진행o

    @OneToMany(mappedBy = "event")
    private List<Product> productList = new ArrayList<>();
}
