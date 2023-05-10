package com.ssg.webpos.dto;

import com.ssg.webpos.domain.enums.PayMethod;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@ToString
public class PaymentsDTO {
  private String name; // 상품이름
  private String pay_method; // 결제 방법
  private BigDecimal amount; // 결제 금액
  private boolean success; // 결제 성공 여부
  private String error_msg; // 실패 시 에러 메시지
  private String imp_uid;

}
