package com.ssg.webpos.domain;

import com.ssg.webpos.domain.enums.RoleHQadmin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "hq_admin")
@NoArgsConstructor
@AllArgsConstructor
public class HQAdmin extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "hq_admin_id")
    private Long id;
    @NotNull
    private String adminNumber;
    @NotNull
    private String name;
    @NotNull
    private String password;
    @NotNull
    @Enumerated
    private RoleHQadmin role;

    private LocalDate startDate;
    private LocalDate endDate;
}
