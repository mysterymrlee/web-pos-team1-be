package com.ssg.webpos.domain.encodingTest;

import com.ssg.webpos.domain.BaseTime;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "test_delivery_address")
public class EncodingDeliveryAddress extends BaseTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "delivery_address_id")
  private Long id;
  private String userName;
  private String address;
  private String postCode;
  private String phoneNumber;

}
