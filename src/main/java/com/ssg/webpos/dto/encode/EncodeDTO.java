package com.ssg.webpos.dto.encode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncodeDTO {
  private String userName;
  private String address;
  private String postCode;
  private String phoneNumber;
}
