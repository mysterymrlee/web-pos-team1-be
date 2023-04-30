package com.ssg.webpos.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name = "delivery_list")
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryAddress extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "delivery_list_id")
    private Long id;
    @NotNull
    private String address;
    @NotNull
    private String phoneNumber;
    @NotNull
    private String name;
    private String requestInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
