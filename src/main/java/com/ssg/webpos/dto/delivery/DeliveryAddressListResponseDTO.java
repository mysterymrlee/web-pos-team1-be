package com.ssg.webpos.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryAddressListResponseDTO {
  private List<DeliveryAddressDTO> deliveryAddressList = new ArrayList<>();
}
