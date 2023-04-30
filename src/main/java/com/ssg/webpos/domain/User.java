package com.ssg.webpos.domain;

import com.ssg.webpos.domain.enums.RoleUser;
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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User extends BaseTime {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long id;
    @NotNull
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String name;
    private LocalDate birth;
    @NotNull
    private String phoneNumber;
    @NotNull
    @Enumerated(EnumType.STRING)
    private RoleUser role;
    @Column(columnDefinition = "integer default 0")
    private int point;

    @OneToMany(mappedBy = "user")
    private List<PointHistory> pointHistoryList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<DeliveryAddress> deliveryAddressList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Order> orderList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Coupon> couponList = new ArrayList<>();

}
