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
  private String requestId;
  private LocalDateTime requestTime;
  private String statusCode;
  private String statusName;
}