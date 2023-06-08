//package com.ssg.webpos.domain.encodingTest;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonInclude;
//import lombok.Data;
//
//@Data
//public class User {
//  private String id;
//  private String name;
//
//  @JsonInclude(JsonInclude.Include.NON_NULL)
//  private String rsaPublicKey;
//
//  @JsonIgnore
//  private String rsaPrivateKey;
//
//  @JsonIgnore
//  private String aseKey;
//
//  public User(String id, String name) {
//    this.id = id;
//    this.name = name;
//  }
//}
