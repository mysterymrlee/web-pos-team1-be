package com.ssg.webpos.dto.gift;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiftRequestDTO {
  private Long posId;
  private Long storeId;
  private String receiver;
  private String phoneNumber;
  private String sender;
}
