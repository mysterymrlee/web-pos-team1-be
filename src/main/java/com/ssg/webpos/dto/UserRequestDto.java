package com.ssg.webpos.dto;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Pattern;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Validated
public class UserRequestDto {
  // 전화번호
  @NotNull
  @Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$", message = "10 ~ 11 자리의 숫자만 입력 가능합니다.")
  String phoneNumber;
}
