package com.ssg.webpos.dto;

import lombok.*;

import java.io.Serializable;

//@RedisHash("person")
//@Data
//public class Person {
//  @Id
//  String id;
//  int total_price;
//  int final_price;
//
//}
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CartDto implements Serializable {
// public class CartDto {
  private String id;
  private int totalPrice;
  private int finalPrice;
}