package com.ssg.webpos.dto.delivery;

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
  private PosStoreCompositeId posStoreCompositeId;
  private String name;
  private String phoneNumber;
}
