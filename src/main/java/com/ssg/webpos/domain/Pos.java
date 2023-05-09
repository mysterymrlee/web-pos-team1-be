package com.ssg.webpos.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "pos")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Pos extends BaseTime {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY) //복합키를 사용하면 @GeneratedValue를 사용할 수 없다.
//    @Column(name = "pos_id")
//    private Long id;
    @EmbeddedId
    private PosStoreCompositeId id;

    @NotNull
    private String serialNumber;
    private LocalDate updateDate;

    // Caused by: org.hibernate.AnnotationException: mappedBy reference an unknown target entity property: com.ssg.webpos.domain.Pos.store in com.ssg.webpos.domain.Store.posList
    // 위와 같은 에러가 생기므로 Store를 작성
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id",insertable = false, updatable = false)
    private Store store;

    @OneToMany(mappedBy = "pos")
    private List<Order> orderList = new ArrayList<>();

}
