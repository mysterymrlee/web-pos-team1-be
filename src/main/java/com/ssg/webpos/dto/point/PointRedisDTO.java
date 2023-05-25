package com.ssg.webpos.dto.point;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString
public class PointRedisDTO implements Serializable {
  private String phoneNumber;

  public PointRedisDTO(PointDTO phoneNumberDTO) {
    this.phoneNumber = phoneNumberDTO.getPhoneNumber();
  }
}
