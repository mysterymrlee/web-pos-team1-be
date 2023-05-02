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
@Table(name = "settlement_month")
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SettlementMonth {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "settlement_month_id")
    private Long id;

    @NotNull
    private int settlementPrice;
    @NotNull
    private LocalDate settlementDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_admin_id")
    private BranchAdmin branchAdmin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;
}
