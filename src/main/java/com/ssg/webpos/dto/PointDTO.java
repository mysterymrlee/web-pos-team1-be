package com.ssg.webpos.dto;

import com.ssg.webpos.domain.PosStoreCompositeId;
import com.ssg.webpos.domain.User;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Validated
public class PointDTO implements Serializable {
  // 전화번호
  private Long posId;
  private Long storeId;
  @NotNull
  @Pattern(regexp = "^01(?:0|1|[6-9])?(\\d{3}|\\d{4})?(\\d{4})$", message = "10 ~ 11 자리의 숫자만 입력 가능합니다.")
  private String phoneNumber;
  private String pointMethod;

  private Long userId;
}

