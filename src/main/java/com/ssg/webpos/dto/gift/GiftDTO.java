package com.ssg.webpos.dto.gift;

import com.ssg.webpos.domain.PosStoreCompositeId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiftDTO implements Serializable {
  private String name;
  private String phoneNumber;
}
