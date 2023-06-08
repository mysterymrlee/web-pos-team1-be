package com.ssg.webpos.dto.gift;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GiftSmsDTO {
  private String receiver;
  private String sender;
  private String entryDeadline;
  private String giftProductName;
  private String orderSerialNumber;
}
