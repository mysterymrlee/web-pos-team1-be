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
  String type;
  String contentType;
  String countryCode;
  String from;
  String content;
  List<MessageDTO> messages;
}