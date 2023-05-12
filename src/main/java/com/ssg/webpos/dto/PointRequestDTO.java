package com.ssg.webpos.dto;

import com.ssg.webpos.domain.PosStoreCompositeId;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class PointRequestDTO {
  private Long posId;
  private Long storeId;

  private String pointMethod;
  private String phoneNumber;

}
