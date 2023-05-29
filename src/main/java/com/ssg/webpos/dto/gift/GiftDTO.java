package com.ssg.webpos.dto.gift;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiftDTO implements Serializable {
  private String receiver;
  private String phoneNumber;
  private String sender;
}
