package com.ssg.webpos.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "store")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(of={"name"})
public class Store extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String branchName;
    private String description;
    private String telNumber;
    @NotNull
    private String postCode;
    @NotNull
    private String address;

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
    private String imageUrl;
    private String businessNumber;
    private String ceoName;
}
