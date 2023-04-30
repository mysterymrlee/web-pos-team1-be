package com.ssg.webpos.domain;

import com.ssg.webpos.domain.enums.RoleAdmin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "branch_admin")
@NoArgsConstructor
@AllArgsConstructor
public class BranchAdmin extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "branch_admin_id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RoleAdmin role;
    @NotNull
    private String adminNumber;
    @NotNull
    private String password;
    @NotNull
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    private LocalDate startDate;
    private LocalDate endDate;
}
