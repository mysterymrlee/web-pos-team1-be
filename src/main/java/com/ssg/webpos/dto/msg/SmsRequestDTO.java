package com.ssg.webpos.dto.msg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SmsRequestDTO {
  private String type;
  private String contentType;
  private String countryCode;
  private String from;
  private String content;
  private List<MessageDTO> messages;
}