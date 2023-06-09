package com.ssg.webpos.dto;

import com.ssg.webpos.domain.PosStoreCompositeId;
import com.ssg.webpos.domain.enums.PayMethod;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@ToString
public class PaymentsDTO {
  private Long posId;
  private Long storeId;
  private String name; // 상품이름
  private String payMethod; // 결제 방법
  private BigDecimal paidAmount; // 결제 금액
  private boolean success; // 결제 성공 여부
  private String pg;
  private int charge;
  private int pointAmount;
  private int giftCardAmount;
  private int couponUsePrice;
  private String cardNumber;
  private String cardName;
  private String merchantUid;

}
