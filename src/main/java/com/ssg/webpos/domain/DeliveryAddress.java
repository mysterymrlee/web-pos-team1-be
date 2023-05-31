package com.ssg.webpos.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Table(name = "delivery_list")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryAddress extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_list_id")
    private Long id;
    @NotNull
    private String address;
    @NotNull
    private String phoneNumber;
    @NotNull
    private String name;
    private String requestInfo;
    private String postCode;
    private byte isDefault; // 0: default 배송지 X, 1: default 배송지 O
    private String deliveryName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Override
    public String toString() {
        return "DeliveryAddress{" +
            "id=" + id +
            ", address='" + address + '\'' +
            ", phoneNumber='" + phoneNumber + '\'' +
            ", name='" + name + '\'' +
            ", requestInfo='" + requestInfo + '\'' +
            ", postCode='" + postCode + '\'' +
            ", isDefault=" + isDefault +
            ", deliveryName='" + deliveryName + '\'' +
            ", user=" + user +
            '}';
    }
}
