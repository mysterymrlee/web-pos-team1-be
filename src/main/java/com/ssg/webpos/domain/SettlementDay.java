package com.ssg.webpos.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "settlement_day")
@ToString(of = {"id", "settlementPrice", "settlementDate", "createdDate"}) //출력할 필드 지정. store는 출렭되지 않음
@EntityListeners(AuditingEntityListener.class)
public class SettlementDay extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settlement_day_id")
    private Long id;

    @NotNull
    private int settlementPrice;
    @NotNull
    private LocalDate settlementDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;
    private int totalOriginPrice;
    private int profit;
}
