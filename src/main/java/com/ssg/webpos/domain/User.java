package com.ssg.webpos.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ssg.webpos.domain.enums.RoleUser;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@ToString
@Table(name = "user")
public class User extends BaseTime {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "point_id")
    private Point point;

    @OneToMany(mappedBy = "user")
    private List<DeliveryAddress> deliveryAddressList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Order> orderList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Coupon> couponList = new ArrayList<>();

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", email='" + email + '\'' +
            ", password='" + password + '\'' +
            ", name='" + name + '\'' +
            ", birth=" + birth +
            ", phoneNumber='" + phoneNumber + '\'' +
            ", role=" + role +
            ", point=" + point +
            '}';
    }
}
