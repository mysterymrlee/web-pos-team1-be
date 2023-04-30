package com.ssg.webpos.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Table(name = "delivery_list")
@AllArgsConstructor
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
