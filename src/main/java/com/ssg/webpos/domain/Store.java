package com.ssg.webpos.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "store")
@NoArgsConstructor
@AllArgsConstructor
public class Store extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "store_id")
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String branchName;
    private String description;
    private String telNumber;
    @NotNull
    private int postCode;
    @NotNull
    private String address;

    @OneToMany(mappedBy = "store")
    private List<Order> orderList = new ArrayList<>();

    @OneToMany(mappedBy = "store")
    private List<SettlementDay> settlementDayList = new ArrayList<>();
    @OneToMany(mappedBy = "store")
    private List<SettlementMonth> settlementMonthList = new ArrayList<>();
    @OneToMany(mappedBy = "store")
    private List<Product> productList = new ArrayList<>();
    @OneToMany(mappedBy = "store")
    private List<StockReport> stockReportList = new ArrayList<>();

    @OneToMany(mappedBy = "store")
    private List<ProductRequest> productRequestList = new ArrayList<>();

    @OneToMany(mappedBy = "store")
    private List<BranchAdmin> branchAdminList = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hq_admin_id")
    private HQAdmin hqAdmin;

    @OneToMany(mappedBy = "store")
    private List<Pos> posList = new ArrayList<>();
}
