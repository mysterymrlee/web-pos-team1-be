package com.ssg.webpos.dto.gift;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GiftDeliveryAddressEntryDTO {
  private String receiver;
  private String phoneNumber;
  private String address;
}
