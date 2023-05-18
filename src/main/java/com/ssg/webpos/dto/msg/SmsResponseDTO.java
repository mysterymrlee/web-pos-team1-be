package com.ssg.webpos.dto.msg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SmsResponseDTO {
  String requestId;
  LocalDateTime requestTime;
  String statusCode;
  String statusName;
}