package com.ssg.webpos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiftRequestDTO implements Serializable {
  private Long storeId;
  private Long posId;
  private String name;
  private String phoneNumber;
}
