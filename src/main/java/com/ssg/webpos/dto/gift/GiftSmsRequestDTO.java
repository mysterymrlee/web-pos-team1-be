package com.ssg.webpos.dto.gift;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GiftSmsRequestDTO {
  private String receiver;
  private String phoneNumber;
  private String address;
  private String postCode;
  private String orderSerialNumber; // delivery 찾는용
}
