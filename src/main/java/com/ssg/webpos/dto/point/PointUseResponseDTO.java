package com.ssg.webpos.dto.point;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class PointUseResponseDTO {
  private int pointAmount;

  public PointUseResponseDTO(int pointAmount) {
    this.pointAmount = pointAmount;
  }
}
